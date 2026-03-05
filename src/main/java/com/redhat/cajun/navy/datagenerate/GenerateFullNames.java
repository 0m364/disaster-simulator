package com.redhat.cajun.navy.datagenerate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class GenerateFullNames {

    private static List<String> fNames = null;

    private static List<String> lNames = null;


    public GenerateFullNames(String fNameFile, String lNameFile) {
        fNames = Collections.unmodifiableList(getListFromFile(fNameFile));
        lNames = Collections.unmodifiableList(getListFromFile(lNameFile));
    }

    public List<String> getListFromFile(String fileName) {
        List<String> temp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(createReader(fileName))) {
            for (String line; (line = br.readLine()) != null; ) {
                temp.add(line);
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
        return lNames.get(ThreadLocalRandom.current().nextInt(lNames.size()));
    }

    public String getNextFirstName() {
        return fNames.get(ThreadLocalRandom.current().nextInt(fNames.size()));
    }


    public String getNextFullName() {
        return getNextFirstName() + " " + getNextLastName();
    }

}
