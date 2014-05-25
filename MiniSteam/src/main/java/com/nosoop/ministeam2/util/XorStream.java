package com.nosoop.ministeam2.util;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author nosoop < nosoop at users.noreply.github.com >
 */
public class XorStream {

    private static int xorOperation(int b, int x) {
        if (b != -1) {
            b = (b ^ x) & 0xFF;
        }
        return b;
    }

    public static class XorInputStream extends FilterInputStream {
        private final byte[] BYTE_PATTERN;
        private int position;

        public XorInputStream(InputStream in, byte[] bytePattern) {
            super(in);
            BYTE_PATTERN = bytePattern;
            position = 0;
        }

        @Override
        public int read() throws IOException {
            int b = in.read();

            return xorOperation(b);
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            int numBytes = in.read(b, off, len);

            if (numBytes <= 0) {
                return numBytes;
            }

            for (int i = 0; i < numBytes; i++) {
                b[off + i] = (byte) xorOperation(b[off + i]);
            }

            return numBytes;
        }

        private int xorOperation(int b) {
            return XorStream.xorOperation(b, 
                    BYTE_PATTERN[position++ % BYTE_PATTERN.length]);
        }
    }
    
    public static class XorOutputStream extends FilterOutputStream {
        
        private final byte[] BYTE_PATTERN;
        private int position;

        public XorOutputStream(OutputStream out, byte[] bytePattern) {
            super(out);
            BYTE_PATTERN = bytePattern;
            position = 0;
        }

        @Override
        public void write(int i) throws IOException {
            out.write(xorOperation(i));
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            for (int i = off; i < off+len; i++) {
                b[off + i] = (byte) xorOperation(b[off + i]);
            }

            out.write(b, off, len);
        }
        
        private int xorOperation(int b) {
            return XorStream.xorOperation(b, 
                    BYTE_PATTERN[position++ % BYTE_PATTERN.length]);
        }
    }
}
