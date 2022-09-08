package springbook.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferReaderCallback {
    Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
