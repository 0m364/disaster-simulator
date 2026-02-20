package com.redhat.cajun.navy.datagenerate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Disaster {
    private static final Logger log = LoggerFactory.getLogger(Disaster.class);

    private static GenerateFullNames fullNames = null;
    public BoundingPolygons boundingPolygons = new BoundingPolygons();
    private Random random = new Random();

    // Simulation Parameters with defaults
    private int minPeople = 1;
    private int maxPeople = 10;
    private double peopleBias = 1.3;
    private double medicalNeededProb = 0.5;
    private int minBoatCapacity = 1;
    private int maxBoatCapacity = 12;
    private double boatCapacityBias = 0.5;
    private double medicalKitProb = 0.5;


    public Disaster(String fNameFile, String lNameFile){
        fullNames = new GenerateFullNames(fNameFile,lNameFile);
    }

    public void setSimulationParameters(int minPeople, int maxPeople, double peopleBias, double medicalNeededProb,
                                        int minBoatCapacity, int maxBoatCapacity, double boatCapacityBias, double medicalKitProb) {
        this.minPeople = minPeople;
        this.maxPeople = maxPeople;
        this.peopleBias = peopleBias;
        this.medicalNeededProb = medicalNeededProb;
        this.minBoatCapacity = minBoatCapacity;
        this.maxBoatCapacity = maxBoatCapacity;
        this.boatCapacityBias = boatCapacityBias;
        this.medicalKitProb = medicalKitProb;
    }


    public Victim generateVictim(){
        Victim v = new Victim();
        v.setVictimName(fullNames.getNextFullName());

        Waypoint point = boundingPolygons.getInternalWaypoint();
        v.setLatLon(point.getY(),point.getX());

        v.setVictimPhoneNumber(GeneratePhoneNumbers.getNextPhoneNumber());
        v.setNumberOfPeople(biasedRandom(minPeople, maxPeople, peopleBias));
        v.setMedicalNeeded(random.nextDouble() < medicalNeededProb);

        return v;
    }

    public List<Responder> generateResponders(int number) {
        List<Responder> responders = new ArrayList<>();
        for(int i=0; i<number; i++){
            responders.add(generateResponder());
        }
        return responders;
    }

    public Responder generateResponder() {
        Responder responder = new Responder();
        Waypoint point = boundingPolygons.getInternalWaypoint();
        responder.setName(fullNames.getNextFullName());
        responder.setPhoneNumber(GeneratePhoneNumbers.getNextPhoneNumber());
        responder.setBoatCapacity(biasedRandom(minBoatCapacity, maxBoatCapacity, boatCapacityBias));
        responder.setMedicalKit(random.nextDouble() < medicalKitProb);
        responder.setLatitude(point.getY());
        responder.setLongitude(point.getX());
        responder.setEnrolled(true);
        responder.setPerson(false);
        responder.setAvailable(true);
        return responder;
    }


    public List<Victim> generateVictims(int number){
        List<Victim> victims = new ArrayList<Victim>();
        for(int i=0; i<number; i++)
            victims.add(generateVictim());
        return victims;
    }

    protected int biasedRandom(int min, int max, double bias) {
        double d = ThreadLocalRandom.current().nextDouble();
        double biased = Math.pow(d, bias);
        return (int) Math.round(min + (max-min)*biased);
    }
 }
