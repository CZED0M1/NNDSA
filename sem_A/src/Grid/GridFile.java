package Grid;

import lombok.Data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
//řádek count + další řádek count atd atd

@Data
public class GridFile{
    private byte[] buffer;
    private final int blockSize;
    private final int headerSize;
    private final int controlBlockSize;
    private final String fileName;
    private int citySizeInBytes;
    private ByteBuffer byteBuffer;
    private int numOfStringChars;
    
    //Řídící blok
    private byte numberOfBlocks;
    private byte firstFreeBlock;
    private byte blockingFactor;

    //Hlavička bloku
    //000000000000000
    //first free index


    private GridIndex<String> gridIndex;


    private int a =0;

    public GridFile(String fileName) {
        this.controlBlockSize = 3; //3*byte
        this.citySizeInBytes = 50; //30 + 4 + 8 +8 double double string(30 znaků fix) int
        this.fileName = fileName;
        this.numOfStringChars=30;
        this.firstFreeBlock=-1;

        //this.blockingFactor = (byte) (blockSize / citySizeInBytes);
        this.blockingFactor = 2;
        this.headerSize = blockingFactor+1;

        this.blockSize = 50*citySizeInBytes;
        this.buffer = new byte[blockSize+headerSize];


        this.gridIndex = new GridIndex<String>(0.0,0.0,70.0,70.0);
        File file = new File(fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                createBlock();
                System.out.println("Soubor byl vytvořen");
                System.out.println("first free block " + firstFreeBlock);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.setLength(50000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readBlock(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek((long) index * blockSize + controlBlockSize);
            raf.read(buffer, 0, blockSize+headerSize);
            byteBuffer = ByteBuffer.wrap(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readControlBlock() {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
            raf.seek(0);
            raf.read(buffer, 0, controlBlockSize);
            byteBuffer = ByteBuffer.wrap(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBlock(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(fileName, "rw")) {
            raf.seek((long) index * blockSize+controlBlockSize);
            raf.write(buffer);
            System.out.println("Blok zapsán");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //find id
    public void addCity(String cityName,int population, Double latitude, Double longitude) {

        readBlock(firstFreeBlock);

        byteBuffer.position(controlBlockSize+blockingFactor);
        int index = byteBuffer.get();
        System.out.println("index " + index);

        //TODO
        if(index > blockingFactor) {
            //byteBuffer.position(numberOfBlocks*blockSize+controlBlockSize+1+headerSize);
            byteBuffer.position(0);
            byteBuffer.position(headerSize+controlBlockSize+4+numberOfBlocks);

            System.out.print(byteBuffer.getDouble()+" " + byteBuffer.getDouble());
            gridIndex.add(cityName,byteBuffer.getDouble(),byteBuffer.getDouble());

            gridIndex.add(cityName+"a",latitude,longitude);
            createBlock();
            addCity(cityName,population,latitude,longitude);
            //TODO save split
            System.out.println("Blok je plný, split");
            writeBlock(firstFreeBlock);
            return;
        }

        //TODO blocky
        byteBuffer.position(controlBlockSize+headerSize-1);
        byte size = byteBuffer.get();
        //size working

        byteBuffer.position(byteBuffer.position()+citySizeInBytes*size);

        byte[] stringBytes = cityName.getBytes(StandardCharsets.UTF_8);
        byteBuffer.put(stringBytes);
        for (int i = cityName.length(); i < numOfStringChars; i++) {
            byteBuffer.put((byte) 0);
        }

        byteBuffer.putInt(population);
        byteBuffer.putDouble(latitude);
        byteBuffer.putDouble(longitude);

        index++;
        byteBuffer.put(blockingFactor + controlBlockSize, (byte) index);

        numberOfBlocks++;
        updateHeader(size,(byte)1);
        writeBlock(firstFreeBlock);
        readCity(a);
        a++;
    }

    public void updateHeader (int index, byte value) {
        readBlock(index);
        byteBuffer.position(blockingFactor);
        byteBuffer.put(value);
    }

    public void readCity(int index) {
        byteBuffer.position(index*citySizeInBytes+controlBlockSize+headerSize+30);
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getDouble());
        System.out.println(byteBuffer.getDouble());
    }

    private void createBlock() {
        numberOfBlocks++;
        if (byteBuffer == null) {
            byteBuffer = ByteBuffer.wrap(buffer);
        }

        updateHeader(0, numberOfBlocks);
        firstFreeBlock++;
        updateHeader(1, firstFreeBlock);

        // Inicializace nového bloku
        byteBuffer.clear();
        for (int i = 0; i <= blockingFactor; i++) {
            byteBuffer.put((byte) 0);
        }
    }

    private void printFullBlock() {
        for (byte b : buffer) {
            System.out.print(b);
        }
    }
}
