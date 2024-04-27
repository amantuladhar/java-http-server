package reader;

import static utils.Constants.LINE_ENDING;
import static utils.Constants.NEW_LINE;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BufferedInputReaderExt implements Closeable {
    private static int END_OF_STREAM_INDICATOR = -1;
    private final BufferedInputStream stream;

    @Getter
    private boolean streamEnded;

    public BufferedInputReaderExt(InputStream in) {
        stream = new BufferedInputStream(in);
        streamEnded = false;
    }

    public byte[] readExact(int length) throws IOException {
        return stream.readNBytes(length);
    }

    public String readExactStr(int length) throws IOException {
        byte[] content = readExact(length);
        return new String(content);
    }

    public ReadData readUntil(int token) throws IOException {
        var buf = new ByteArrayOutputStream();
        int curChar;
        int readCount = 0;
        while ((curChar = stream.read()) != END_OF_STREAM_INDICATOR) {
            readCount++;
            buf.write(curChar);
            if (curChar == token) {
                break;
            }
        }
        return new ReadData(buf.toByteArray(), readCount);
    }

    public String readLine() throws IOException {
        return readUntil(NEW_LINE).contentAsStr();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    public static record ReadData(byte[] content, int readCount) {
        public String contentAsStr() {
            int length = content.length - LINE_ENDING.length();
            byte[] resized = Arrays.copyOf(content, length);
            return new String(resized);
        }

        public boolean isEmpty() {
            return contentAsStr().isBlank();
        }
    }
}
