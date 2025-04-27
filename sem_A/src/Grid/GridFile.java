package Grid;

import DataStructures.Location;
import lombok.Data;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

@Data
public class GridFile implements Serializable {
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

    private GridIndex<Byte> gridIndex;

    public GridFile(String fileName) {
        this.fileName = fileName;
        File indexFile = new File(fileName + "_index.dat");
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
        // Načtení GridIndex
        if (indexFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile))) {
                gridIndex = (GridIndex<Byte>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
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
            gridIndex.add(firstFreeBlock,latitude,longitude);
            saveGridIndex();
        }

        if (count >= blockingFactor) {
            createBlock();
            addCity(cityName, population, latitude, longitude);
            gridIndex.add(firstFreeBlock,latitude,longitude);
            readBlock(firstFreeBlock - 1);
            saveGridIndex();
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


    public void findRange(Location locationStart, Location locationEnd) {
        Byte startIndex = gridIndex.findIndexInGrid(locationStart);
        Byte endIndex = gridIndex.findIndexInGrid(locationEnd);


        StringBuilder message= new StringBuilder();
            for (int blockIndex = startIndex; blockIndex <= endIndex; blockIndex++) {
                readBlock(blockIndex);

                byte count = byteBuffer.get(0);
                if (count <= 0) {
                    continue; // Tento blok je prázdný
                }

                for (int cityIndex = 0; cityIndex < count; cityIndex++) {
                    int offset = headerSize + cityIndex * citySizeInBytes;
                    byteBuffer.position(offset);

                    byte[] cityNameBytes = new byte[numOfStringChars];
                    byteBuffer.get(cityNameBytes);
                    String cityName = new String(cityNameBytes, StandardCharsets.UTF_8).trim();
                    int population = byteBuffer.getInt();
                    double latitude = byteBuffer.getDouble();
                    double longitude = byteBuffer.getDouble();

                    // Podmínka, zda je město v zadaném rozsahu
                    if (latitude >= locationStart.getX() && latitude <= locationEnd.getX() &&
                            longitude >= locationStart.getY() && longitude <= locationEnd.getY()) {
                        if(!cityName.isEmpty()) {


                            message.append("---" + "City: ").append(cityName).append("\n").append("Population: ").append(population).append("\n").append("Latitude: ").append(latitude).append("\n").append("Longitude: ").append(longitude).append("\n");
                        }
                    }
                }
            }
        JTextArea textArea = new JTextArea(String.valueOf(message));
        textArea.setEditable(false);  // Textové pole nebude editovatelné
        textArea.setWrapStyleWord(true);  // Slova se budou lámat na nový řádek
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);  // Scroll bar pro text
        JOptionPane.showMessageDialog(null, scrollPane, "Detail města", JOptionPane.INFORMATION_MESSAGE);
    }

    public void saveGridIndex() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName + "_index.dat"))) {
            oos.writeObject(gridIndex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AbstractMap.SimpleEntry<String,Location>> getCities(int  row, int col) {
        Byte index = gridIndex.getGrid().get(row).get(col).getKey();
        List<AbstractMap.SimpleEntry<String,Location>> cities = new ArrayList<>();
        for (int i=0; i<blockingFactor; i++) {

            readBlock(index);

            byte count = byteBuffer.get(0);
            if (i >= count) {
                System.out.println("City index out of range.");
            }

            int offset = headerSize + i * citySizeInBytes;
            byteBuffer.position(offset);

            byte[] cityNameBytes = new byte[numOfStringChars];
            byteBuffer.get(cityNameBytes);
            String cityName = new String(cityNameBytes, StandardCharsets.UTF_8).trim();
            int population = byteBuffer.getInt();
            double latitude = byteBuffer.getDouble();
            double longitude = byteBuffer.getDouble();

            cities.add(new AbstractMap.SimpleEntry<>(cityName,new Location(latitude,longitude)));

        }
        return cities;
    }

    public void deleteCity(Location location) {
        Byte index = gridIndex.findIndexInGrid(location);
        readBlock(index);

        byte count = byteBuffer.get(0);
        if (count <= 0) {
            return; // Tento blok je prázdný
        }

        for (int cityIndex = 0; cityIndex < count; cityIndex++) {
            int offset = headerSize + cityIndex * citySizeInBytes;
            byteBuffer.position(offset);

            byte[] cityNameBytes = new byte[numOfStringChars];
            byteBuffer.get(cityNameBytes);
            String cityName = new String(cityNameBytes, StandardCharsets.UTF_8).trim();
            int population = byteBuffer.getInt();
            double latitude = byteBuffer.getDouble();
            double longitude = byteBuffer.getDouble();

            // Podmínka, zda je město v zadaném rozsahu
            if (latitude == location.getX() && longitude == location.getY()) {


                deleteCity(index,cityIndex);

            }

        }
    }

    private void deleteCity(int blockIndex,int cityIndex) {

        byte count = byteBuffer.get(0);
        if (cityIndex >= count) {
            System.out.println("City index out of range.");
            return;
        }

        int offset = headerSize + cityIndex * citySizeInBytes;
        byteBuffer.position(offset);


        byteBuffer.put(new byte[30]);
        byteBuffer.put(new byte[4]);
        byteBuffer.put(new byte[8]);
        byteBuffer.put(new byte[8]);
        writeBlock(blockIndex);

    }



    public void findPoint(Location location) {
        findRange(location, location);
        
    }
}
