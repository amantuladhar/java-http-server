package reader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BufferedInputReaderExt implements Closeable {
    private static int END_OF_STREAM_INDICATOR = -1;
    private final BufferedInputStream stream;

    @Getter
    private boolean eofReached;

    public BufferedInputReaderExt(InputStream in) {
        stream = new BufferedInputStream(in);
        eofReached = false;
    }

    public ReadData readUntil(int token) throws IOException {
        var buf = new ByteArrayOutputStream();
        int c;
        int readCount = 0;
        while ((c = stream.read()) != END_OF_STREAM_INDICATOR && c != token) {
            readCount++;
            buf.write(c);
        }
        eofReached = c == END_OF_STREAM_INDICATOR;
        return new ReadData(buf.toByteArray(), readCount, eofReached);
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public static record ReadData(byte[] content, int readCount, boolean eofReached) {
    }
}
