package springbook.template;

import lombok.val;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filePath) throws IOException {
//        BufferReaderCallback callback = new BufferReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer sum = 0;
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    sum += Integer.valueOf(line);
//                }
//                return sum;
//            }
//        };
//        return fileReadTemplate(filePath, callback);

        LineCallback<Integer> callback = (line, value) -> value + Integer.valueOf(line);
        return lineReadTemplate(filePath, callback, 0);
    }

    public Integer calcMultiply(String filePath) throws IOException {
//        BufferReaderCallback callback = new BufferReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer multiply = 1;
//                String line;
//
//                while ((line = br.readLine()) != null) {
//                    multiply *= Integer.valueOf(line);
//                }
//                return multiply;
//            }
//        };
//        return fileReadTemplate(filePath, callback);

        LineCallback<Integer> callback = (line, value) -> value * Integer.valueOf(line);
        return lineReadTemplate(filePath, callback, 1);
    }

    public String concat(String filePath) throws IOException {
        LineCallback<String> callback = (line, value) -> value + line;
        return lineReadTemplate(filePath, callback, "");
    }

    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T val) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            T res = val;
            String line;

            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public Integer fileReadTemplate(String filePath, BufferReaderCallback callback) throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filePath));
            Integer ret = callback.doSomethingWithReader(br);
            return  ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
