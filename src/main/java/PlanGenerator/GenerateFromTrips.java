package PlanGenerator;

import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GenerateFromTrips {
    final static double AVERAGE_EGRESS_TIME = 60*10;

    public static void main(String[] args) {
        String inputCRS = "EPSG:4326"; // WGS84
        String outputCRS = "EPSG:32635";
        CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation(inputCRS, outputCRS);
        String inputStations = "input/trips/stops.csv";
        String inputTrips = "input/trips/TRIPS3.csv";
        Map stopMap = new HashMap();
        Map<String, Passenger> passengerMap = new HashMap();

        readStations(inputStations, stopMap);
        readTrips(inputTrips, passengerMap);
        createPopulation(passengerMap, ct, stopMap);
        //writePopulation();

    }

    private static void createPopulation(Map<String, Passenger> passengerMap, CoordinateTransformation ct, Map stopMap) {
        Scenario scenario = ScenarioUtils.createScenario(ConfigUtils.createConfig());
        Population population = scenario.getPopulation();
        PopulationFactory  populationFactory = population.getFactory();
        Iterator iterator = passengerMap.values().iterator();
        while (iterator.hasNext()){
            Passenger passenger = (Passenger) iterator.next();
            Person person = populationFactory.createPerson(Id.createPersonId(passenger.getPassengerId()));
            Plan plan = populationFactory.createPlan();
            person.addPlan(plan);
            population.addPerson(person);
            Iterator tripIterator = passenger.tripList.iterator();
            int tripIndex = 0;
            while (tripIterator.hasNext()){
                tripIndex++;
                Trip trip = (Trip) tripIterator.next();
                Stop startStop = (Stop) stopMap.get(trip.startStopId);
                Coord transformedCoord = ct.transform(startStop.getCoord());
                Coord randomizedTransformedCoord = randomizeCoord(transformedCoord);
                String activityType;
                boolean isLastActivity = false;
                if ((tripIndex == 1) && (tripIndex >= passenger.tripList.size())){
                    activityType = "h";
                } else {
                    if (trip.getStartTime() < 8 * 3600) {
                        double random = Math.random();
                        if (random < 0.95) {
                            activityType = "h";
                        } else activityType = "w";
                    } else if (trip.getStartTime() < 14 * 3600){
                        double random = Math.random();
                        if (random < 0.5) {
                            activityType = "e";
                        } else activityType = "w";
                    } else if (trip.getStartTime() < 20 * 3600){
                        double random = Math.random();
                        if (random < 0.2) {
                            activityType = "e";
                        } else if (random < 0.4){
                            activityType = "s";
                        } else activityType = "w";
                    } else {
                        double random = Math.random();
                        if (random < 0.2) {
                            activityType = "w";
                        } else activityType = "s";
                    }
                }
                Activity activity = populationFactory.createActivityFromCoord(activityType, randomizedTransformedCoord);
                activity.setEndTime(trip.startTime);
                plan.addActivity(activity);
                Leg leg = populationFactory.createLeg("pt");
                plan.addLeg(leg);

                if (!tripIterator.hasNext()){
                    if ((passenger.tripList.size() > 1) && tripIndex > 0){
                    Activity firstActivity = (Activity) person.getPlans().get(0).getPlanElements().get(0);
                    Activity lastActivity = populationFactory.createActivityFromCoord(firstActivity.getType(), firstActivity.getCoord());
                    lastActivity.setEndTime(25 * 3600);
                    plan.addActivity(lastActivity);
                    } else {
                        Stop endStop = (Stop) stopMap.get(trip.endStopId);
                        Coord endStopCoord = endStop.getCoord();
                        Coord transformedEndStopCoord = ct.transform(endStopCoord);
                        Coord randomizedTransformedEndStopCoord = randomizeCoord(transformedEndStopCoord);
                        Activity lastActivity = populationFactory.createActivityFromCoord("h", randomizedTransformedEndStopCoord);
                        plan.addActivity(lastActivity);
                    }
                }

            }

        }
        PopulationWriter populationWriter = new PopulationWriter(population);
        populationWriter.writeV6("output/outputPopulationFromValidations1.xml");
    }

    private static Coord randomizeCoord(Coord transformedCoord) {
        double newX = transformedCoord.getX() + (( (-1) + (Math.random() * 2)) * 5 * (5 / 3.6));
        double newY = transformedCoord.getY() + (( (-1) + (Math.random() * 2)) * 5 * (5 / 3.6));
        Coord coord = CoordUtils.createCoord(newX, newY);
        return coord;
    }

    private static void readTrips(String inputTrips, Map passengerMap) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputTrips));
            String line = null;
            int lineNumber = 0;
            while ((line = bufferedReader.readLine()) != null){
                lineNumber++;
                String[] items = line.split(",");
                String cardId = items[0];
                Trip trip = new Trip(lineNumber, cardId);
                String[] startTimeRaw = items[1].split(":");
                int startHour = Integer.parseInt(startTimeRaw[0]);
                int startMinute = Integer.parseInt(startTimeRaw[1]);
                double startTime = startHour * 3600 + startMinute * 60 - (Math.random() * 10) - AVERAGE_EGRESS_TIME;
                String startStopId = items[2];
                String endStopId = items[4];
                trip.setStartStopId(startStopId);
                trip.setStartTime(startTime);
                trip.setEndStopId(endStopId);


                Iterator iterator = passengerMap.keySet().iterator();
                boolean alreadyHasTrips = false;
                while (iterator.hasNext()) {
                    String passengerId = (String) iterator.next();
                    if (passengerId.equals(cardId)) {
                        Passenger passenger = (Passenger) passengerMap.get(passengerId);
                        passenger.tripList.add(trip);
                        alreadyHasTrips = true;
                    }
                }
                if (!alreadyHasTrips) {
                    Passenger newPassenger = new Passenger(cardId);
                    newPassenger.tripList.add(trip);
                    passengerMap.put(newPassenger.getPassengerId(), newPassenger);
                }

            }
            sortPassengerTrips(passengerMap);
            System.out.println("read the file with trips");
            cleanNullStops(passengerMap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } ;
    }

    private static void cleanNullStops(Map passengerMap) {
        Iterator iterator = passengerMap.keySet().iterator();
        while (iterator.hasNext()){
            String passengerId = (String) iterator.next();
            Passenger passenger = (Passenger) passengerMap.get(passengerId);
            Iterator tripIterator = passenger.tripList.iterator();
            int nextTripNumber = 0;
            while (tripIterator.hasNext()){
                nextTripNumber++;
                Trip trip = (Trip) tripIterator.next();
                if (trip.getEndStopId().equals("null")) {
                    if (nextTripNumber >= passenger.tripList.size()){
                        nextTripNumber = 0;
                    }
                    Trip nextTrip = (Trip) passenger.tripList.get(nextTripNumber);
                    trip.setEndStopId(nextTrip.startStopId);
                }
                if (trip.getEndStopId().equals(trip.getStartStopId())){
                    tripIterator.remove();
                }
            }
         }
         System.out.println("cleaned trips");
    }

    private static void sortPassengerTrips(Map passengerMap) {
        Iterator iterator = passengerMap.keySet().iterator();
        while (iterator.hasNext()){
            Passenger passenger = (Passenger) passengerMap.get(iterator.next());
            Collections.sort(passenger.tripList);
        }
        System.out.println("sorted!");
    }

    private static void readStations(String inputStations, Map stopMap) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inputStations));
            String line = null;
            int lineNumber = 0;

            while((line = bufferedReader.readLine()) != null) {
                lineNumber++;
                String[] items = line.split(",");

                String stopId = items[0];
                String stopName = items[2];
                String mode = items[3];
                Double y = Double.parseDouble(items[4]);
                Double x = Double.parseDouble(items[5]);
                Coord stopCoord = new Coord(x, y);
                Stop stop = new Stop(stopId, stopName, mode, stopCoord);
                stopMap.put(stopId, stop);
            }

            System.out.println("end");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
