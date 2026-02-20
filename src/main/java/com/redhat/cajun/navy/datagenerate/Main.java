package com.redhat.cajun.navy.datagenerate;


import org.apache.commons.cli.*;
import io.vertx.reactivex.core.Vertx;

public class Main {


    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option mode = new Option("m", "mode", true, "server|cli");
        mode.setRequired(true);
        options.addOption(mode);

        Option generate = new Option("g", "generate", true, "number of victims to generate");
        generate.setRequired(true);
        options.addOption(generate);

        Option scrape = new Option("s", "scrape", true, "file path to scraped data json");
        scrape.setRequired(false);
        options.addOption(scrape);

        Option geojson = new Option("map", "geojson", true, "file path to geojson map file");
        geojson.setRequired(false);
        options.addOption(geojson);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Main -m server|cli -g NUMBER", options);

            System.exit(1);
        }

        int number = Integer.parseInt(cmd.getOptionValue("g"));

        Disaster disaster = new Disaster("fnames.txt","lnames.txt");
        if (cmd.hasOption("scrape")) {
            String scrapeFile = cmd.getOptionValue("scrape");
            DataScraper scraper = new DataScraper();
            ScrapedData data = scraper.scrape(scrapeFile);
            if (data != null) {
                disaster.applyScrapedData(data);
            }
        }

        if (cmd.hasOption("geojson")) {
            String geoJsonFile = cmd.getOptionValue("geojson");
            GeoJsonLoader loader = new GeoJsonLoader();
            java.util.List<Zone> zones = loader.load(geoJsonFile);
            if (!zones.isEmpty()) {
                disaster.boundingPolygons.setInclusionZones(zones);
            }
        }

        switch(cmd.getOptionValue("m")) {
            case "server":
                Vertx vertx = Vertx.vertx();
                // Pass scraped file path via system property if needed, or rely on config
                if (cmd.hasOption("scrape")) {
                    System.setProperty("simulation.scraped.data.file", cmd.getOptionValue("scrape"));
                }
                if (cmd.hasOption("geojson")) {
                    System.setProperty("simulation.geojson.file", cmd.getOptionValue("geojson"));
                }
                vertx.rxDeployVerticle(MainVerticle.class.getName())
                        .subscribe();
                break;
            case "cli":
                System.out.println("Generating Victims List in Json");
                System.out.println(disaster.generateVictims(number));
                break;
            default: System.err.println("Incorrect mode");
        }
    }

}