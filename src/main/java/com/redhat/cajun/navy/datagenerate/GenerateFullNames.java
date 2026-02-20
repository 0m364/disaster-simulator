package com.redhat.cajun.navy.datagenerate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GenerateFullNames {

    private static Map<Integer, String> fNames = null;

    private static Map<Integer, String> lNames = null;


    public GenerateFullNames(String fNameFile, String lNameFile) {
        fNames = Collections.unmodifiableMap(getMapFromFile(fNameFile));
        lNames = Collections.unmodifiableMap(getMapFromFile(lNameFile));
    }

    public Map<Integer, String> getMapFromFile(String fileName) {
        Map<Integer, String> temp = new HashMap<>();
        int count = 0;
        try (BufferedReader br = new BufferedReader(createReader(fileName))) {
            for (String line; (line = br.readLine()) != null; ) {
                temp.put(count, line);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    private Reader createReader(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            return new FileReader(file);
        }
        InputStream is = getClass().getResourceAsStream("/" + fileName);
        if (is != null) {
            return new InputStreamReader(is);
        }
        is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            return new InputStreamReader(is);
        }
        throw new FileNotFoundException("File not found: " + fileName);
    }

    public String getNextLastName() {
        return lNames.get(new Random().nextInt(lNames.size()));
    }

    public String getNextFirstName() {
        return fNames.get(new Random().nextInt(fNames.size()));
    }


    public String getNextFullName() {
        return getNextFirstName() + " " + getNextLastName();
    }

}
