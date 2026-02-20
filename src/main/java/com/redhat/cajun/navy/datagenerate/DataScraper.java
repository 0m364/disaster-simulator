package com.redhat.cajun.navy.datagenerate;

import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataScraper {

    public ScrapedData scrape(String filePath) {
        if (filePath == null) {
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JsonObject json = new JsonObject(content);

            ScrapedData data = new ScrapedData();
            data.setAvgPeoplePerHousehold(json.getDouble("avgPeoplePerHousehold", 2.5));
            data.setMedicalNeedRate(json.getDouble("medicalNeedRate", 0.1));
            data.setResponderDensity(json.getDouble("responderDensity", 0.05));
            data.setRegionalRiskFactor(json.getDouble("regionalRiskFactor", 1.0));

            return data;
        } catch (IOException e) {
            System.err.println("Error reading scraped data file: " + e.getMessage());
            return null;
        }
    }
}
