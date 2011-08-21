package com.mundi4.mpq;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.fail;

public class MpqFileTest {

    int nThreads = 5;

    String filename = "D:\\Games\\World of Warcraft\\Data\\koKR\\lichking-locale-koKR.MPQ";

    @Test
    public void testReadFilesUsingThreads() throws IOException {
        System.gc();
        long start = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        MpqFile mpq = new MpqFile(filename);
        Iterator<MpqEntry> iter = mpq.iterator();
        while (iter.hasNext()) {
            MpqEntry entry = iter.next();
            executor.execute(new ReadFileCommand(mpq, entry));
        }
        executor.shutdown();
        try {
            while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } finally {
            try {
                mpq.close();
            } catch (Exception e2) {
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("elapsed(using thread):" + (end - start));
    }

    @Test
    public void testReadFiles() throws IOException {
        System.gc();
        long start = System.currentTimeMillis();
        MpqFile mpq = new MpqFile(filename);
        try {
            Iterator<MpqEntry> iter = mpq.iterator();
            while (iter.hasNext()) {
                MpqEntry entry = iter.next();
                readFile(mpq, entry);
            }
        } finally {
            try {
                mpq.close();
            } catch (Exception e2) {
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("elapsed:" + (end - start));
    }

    private void readFile(MpqFile mpq, MpqEntry entry) {
        InputStream is = null;
        try {
            is = mpq.getInputStream(entry);
            int read = 0;
            while (skipOrRead(is)) {
                read++;
            }
        } catch (Exception e) {
            fail(e.toString());
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    private class ReadFileCommand implements Runnable {

        MpqFile mpq;
        MpqEntry entry;

        public ReadFileCommand(MpqFile mpq, MpqEntry entry) {
            this.mpq = mpq;
            this.entry = entry;
        }

        @Override
        public void run() {
            readFile(mpq, entry);
        }
    }

    private Random random = new Random(System.currentTimeMillis());

    private boolean skipOrRead(InputStream in) throws IOException {
        if (random.nextBoolean()) {
            long skipped = in.skip(1);
            return skipped == 1;
        } else {
            return in.read() != -1;
        }
    }

}
