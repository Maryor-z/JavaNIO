package com.maryor;

import javax.print.attribute.standard.RequestingUserName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.RandomAccess;

public class Main {
    public static void main(String[] args) {

        try {
            Pipe pipe = Pipe.open();

            Runnable writer = new Runnable() {
                @Override
                public void run() {
                    try {
                        Pipe.SinkChannel sinkChannel = pipe.sink();
                        ByteBuffer buffer = ByteBuffer.allocate(56);

                        for (int i = 0; i < 10; i++) {
                            String currentTime = "The time is: " + System.currentTimeMillis();

                            buffer.put(currentTime.getBytes());
                            buffer.flip();

                            while (buffer.hasRemaining()) {
                                sinkChannel.write(buffer);
                            }
                            buffer.flip();
                            Thread.sleep(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

                Runnable reader = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Pipe.SourceChannel sourceChannel = pipe.source();
                            ByteBuffer buffer = ByteBuffer.allocate(56);

                            for (int i=0; i<10; i++) {
                                int byteRead = sourceChannel.read(buffer);
                                byte[] timeString = new byte[byteRead];
                                buffer.flip();
                                buffer.get(timeString);
                                System.out.println("Reader Thread: " + new String(timeString));
                                buffer.flip();
                                Thread.sleep(100);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            };

            new Thread(writer).start();
            new Thread(reader).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}