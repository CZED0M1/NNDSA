package Grid;

import DataStructures.Location;
import lombok.Data;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class GridFile {
    private final String fileName;
    private final int citySizeInBytes = 30 + 4 + 8 + 8; // cityName + population + latitude + longitude
    private final int numOfStringChars = 30;
    private final int controlBlockSize = 3; // numberOfBlocks, firstFreeBlock, blockingFactor
    private final int headerSize = 1; // count of cities in block
    private final int blockingFactor = 2; // kolik záznamů se vejde do bloku
    private final int blockSize = citySizeInBytes * blockingFactor;
    private byte[] buffer;
    private ByteBuffer byteBuffer;

    // řídicí blok
    private byte numberOfBlocks;
    private byte firstFreeBlock;

    private GridIndex<String> gridIndex;

    public GridFile(String fileName) {
        this.fileName = fileName;
        this.buffer = new byte[blockSize + headerSize];
        this.gridIndex = new GridIndex<>(0.0, 0.0, 70.0, 70.0);

        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                    raf.setLength(50000);
                }
                numberOfBlocks = 0;
                firstFreeBlock = 0;
                writeControlBlock();
                createBlock();
            } else {
                loadControlBlock();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadControlBlock() {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(0);
            numberOfBlocks = raf.readByte();
            firstFreeBlock = raf.readByte();
            raf.readByte();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeControlBlock() {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek(0);
            raf.writeByte(numberOfBlocks);
            raf.writeByte(firstFreeBlock);
            raf.writeByte(blockingFactor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void readBlock(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(controlBlockSize + (long) index * (blockSize + headerSize));
            raf.readFully(buffer, 0, blockSize + headerSize);
            byteBuffer = ByteBuffer.wrap(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeBlock(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek(controlBlockSize + (long) index * (blockSize + headerSize));
            raf.write(buffer, 0, blockSize + headerSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createBlock() {
        buffer = new byte[blockSize + headerSize];
        byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.put((byte) 0);
        writeBlock(numberOfBlocks);
        firstFreeBlock = numberOfBlocks;
        numberOfBlocks++;
        writeControlBlock();
    }

    public void addCity(String cityName, int population, double latitude, double longitude) {
        readBlock(firstFreeBlock);

        byte count = byteBuffer.get(0);
        if (count == 0 && firstFreeBlock == 0) {
            gridIndex.add(cityName,latitude,longitude);
        }

        if (count >= blockingFactor) {
            createBlock();
            addCity(cityName, population, latitude, longitude);
            gridIndex.add(cityName,latitude,longitude);
            readBlock(firstFreeBlock - 1);
            return;
        }

        int offset = headerSize + count * citySizeInBytes;
        byteBuffer.position(offset);

        byte[] cityNameBytes = cityName.getBytes(StandardCharsets.UTF_8);
        byteBuffer.put(cityNameBytes);
        for (int i = cityName.length(); i < numOfStringChars; i++) {
            byteBuffer.put((byte) 0);
        }
        byteBuffer.putInt(population);
        byteBuffer.putDouble(latitude);
        byteBuffer.putDouble(longitude);

        byteBuffer.put(0, (byte) (count + 1)); // aktualizuj počet záznamů

        writeBlock(firstFreeBlock);
        readCity(firstFreeBlock, 0);
    }

    public void readCity(int blockIndex, int cityIndex) {
        readBlock(blockIndex);

        byte count = byteBuffer.get(0);
        if (cityIndex >= count) {
            System.out.println("City index out of range.");
            return;
        }

        int offset = headerSize + cityIndex * citySizeInBytes;
        byteBuffer.position(offset);

        byte[] cityNameBytes = new byte[numOfStringChars];
        byteBuffer.get(cityNameBytes);
        String cityName = new String(cityNameBytes, StandardCharsets.UTF_8).trim();
        int population = byteBuffer.getInt();
        double latitude = byteBuffer.getDouble();
        double longitude = byteBuffer.getDouble();

        System.out.println("City: " + cityName);
        System.out.println("Population: " + population);
        System.out.println("Latitude: " + latitude);
        System.out.println("Longitude: " + longitude);
    }

    private int findCityIndex(int block, Location location) {
        readBlock(block); // Načteme blok podle jeho indexu
        byte count = byteBuffer.get(0); // Počet měst v tomto bloku

        // Pokud je count menší než 1, znamená to, že v bloku nejsou žádná města
        if (count <= 0) {
            System.out.println("No cities in this block.");
            return -1;
        }

        // Projdeme všechna města v bloku
        for (int i = 0; i < count; i++) {
            int offset = headerSize + i * citySizeInBytes;
            byteBuffer.position(offset);

            // Načteme data města
            byte[] cityNameBytes = new byte[numOfStringChars];
            byteBuffer.get(cityNameBytes);
            byteBuffer.getInt(); // Přečteme populaci, ale nebudeme ji používat
            double latitude = byteBuffer.getDouble();
            double longitude = byteBuffer.getDouble();

            // Porovnáme souřadnice města s těmi, které hledáme
            if (latitude == location.getX() && longitude == location.getY()) {
                return i; // Pokud najdeme město s odpovídajícími souřadnicemi, vrátíme index
            }
        }

        // Pokud nenajdeme město s odpovídajícími souřadnicemi, vrátíme -1
        return -1;
    }

    public void getCity(int blockNumber, Location location) {
        // Získejte index bloku v gridu
        int[] blockIndex = gridIndex.findIndexInGrid(location);
        if (blockIndex == null || blockIndex.length != 2) {
            JOptionPane.showMessageDialog(null, "Invalid location.");
            return;
        }

        int row = blockIndex[0];
        int col = blockIndex[1];

        // Kontrola validity indexu a null hodnot
        if (gridIndex.getGrid() == null ||
                gridIndex.getGrid().size() <= row ||
                gridIndex.getGrid().get(row) == null ||
                gridIndex.getGrid().get(row).size() <= col) {
            JOptionPane.showMessageDialog(null, "Grid index out of range.");
            return;
        }

        // Načteme blok podle blockNumber
        readBlock(blockNumber);

        byte count = byteBuffer.get(0); // Počet měst v bloku
        if (count <= 0) {
            JOptionPane.showMessageDialog(null, "No cities in this block.");
            return;
        }

        // Hledáme město podle souřadnic
        int cityIndex = findCityIndex(blockNumber, location);

        if (cityIndex == -1 || cityIndex >= count) {
            JOptionPane.showMessageDialog(null, "City index out of range.");
            return;
        }

        // Výpočet offsetu pro dané město
        int offset = headerSize + cityIndex * citySizeInBytes;

        // Ochrana proti zápornému offsetu
        if (offset < 0 || offset >= buffer.length) {
            JOptionPane.showMessageDialog(null, "Invalid offset.");
            return;
        }

        byteBuffer.position(offset);

        // Načteme údaje o městě
        byte[] cityNameBytes = new byte[numOfStringChars];
        byteBuffer.get(cityNameBytes);
        String cityName = new String(cityNameBytes, StandardCharsets.UTF_8).trim();
        int population = byteBuffer.getInt();
        double latitude = byteBuffer.getDouble();
        double longitude = byteBuffer.getDouble();

        // Sestavení textu pro zobrazení v JOptionPane
        String message = "City: " + cityName + "\n" +
                "Population: " + population + "\n" +
                "Latitude: " + latitude + "\n" +
                "Longitude: " + longitude;

        // Zobrazení dialogu s informacemi
        JOptionPane.showMessageDialog(null, message);
    }


    public void findPoint(Location location) {
        int[] index = gridIndex.findIndexInGrid(location);
        if (index == null || index.length != 2 || index[0] < 0 || index[1] < 0) {
            JOptionPane.showMessageDialog(null, "Invalid location.");
            return;
        }

        int row = index[0];
        int col = index[1];

        if (gridIndex.getGrid() == null || gridIndex.getGrid().size() <= row || gridIndex.getGrid().get(row) == null || gridIndex.getGrid().get(row).size() <= col) {
            JOptionPane.showMessageDialog(null, "Grid index out of range.");
            return;
        }

        int blockIndex = row * gridIndex.getGrid().get(row).size() + col;
        getCity(blockIndex, location);
    }
}
