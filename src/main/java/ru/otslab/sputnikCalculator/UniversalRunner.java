package ru.otslab.sputnikCalculator;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by volot on 12.05.2017.
 */
public class UniversalRunner {
    public static void main(String[] args) {
        double scaleCoefficient = 1.0;
        double populationSampleCar = 1.0;
        double populationSamplePT = 0.00001;
        boolean scalePopulation = true;
        boolean removePersonOnMode = true;

        boolean calculateCar = false;
        if (calculateCar == true) {
            for (int year = 2036; year < 2037; year = year + 5) {
                calcCar(scaleCoefficient, populationSampleCar, year);
            }
        }
        boolean calculatePT = true;
        if (calculatePT == true) {
            for (int year = 2021; year < 2032; year = year + 5) {
                calcPT(scaleCoefficient, populationSamplePT, year);
            }
        }


    }

    private static void calcCar (double scaleCoefficient, double populationSample, int year) {
        String configFile = "config_horizon_"+ year + "_1_car.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        //config.global().setNumberOfThreads(12);
        config.qsim().setFlowCapFactor(scaleCoefficient * 1.5);
        config.qsim().setStorageCapFactor(scaleCoefficient * 2);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();


        AgentsTripModeModifier modeModifier = new AgentsTripModeModifier(population);
        modeModifier.clean("pt");

        //Population drawedPopulation = PopulationUtils.createPopulation(config);
        List<Id<Person>> personIdList2 = new LinkedList<Id<Person>>();

        Iterator personIterator = population.getPersons().values().iterator();
        while (personIterator.hasNext()) {
            Person person = (Person) personIterator.next();
            personIdList2.add(person.getId());
           /*
            Id<Person> toAddId = (Id<Person>) randomDrawIterator.next();
            Person toAddPerson = population.getPersons(Map<Id<Person>, args> );
            drawedPopulation.addPerson(toRemoveId);
            */
        }
        List<Id<Person>> randomDraw = pickNRandom(personIdList2, personIdList2.size() * (1-populationSample));
        Iterator randomDrawIterator = randomDraw.iterator();
        while (randomDrawIterator.hasNext()) {
            Id<Person> toRemoveId = (Id<Person>) randomDrawIterator.next();
            log.println("Removing the person " + toRemoveId);
            population.removePerson(toRemoveId);
    }
        Controler controler = new Controler(scenario);
        controler.run();
    }

    private static void calcPT (double scaleCoefficient, double populationSample, int year) {
        String configFile = "config_horizon_"+ year + "_1_pt.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        //config.global().setNumberOfThreads(12);
        config.qsim().setFlowCapFactor(scaleCoefficient);
        config.qsim().setStorageCapFactor(scaleCoefficient * 2);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();

        List<Id<Person>> personIdList = new LinkedList<Id<Person>>();
        AgentsTripModeModifier modeModifier = new AgentsTripModeModifier(population);
        modeModifier.clean("car");

        //Population drawedPopulation = PopulationUtils.createPopulation(config);
        List<Id<Person>> personIdList2 = new LinkedList<Id<Person>>();

        Iterator personIterator = population.getPersons().values().iterator();
        while (personIterator.hasNext()) {
            Person person = (Person) personIterator.next();
            personIdList2.add(person.getId());
           /*
            Id<Person> toAddId = (Id<Person>) randomDrawIterator.next();
            Person toAddPerson = population.getPersons(Map<Id<Person>, args> );
            drawedPopulation.addPerson(toRemoveId);
            */
        }
        List<Id<Person>> randomDraw = pickNRandom(personIdList2, personIdList2.size() * (populationSample));
        Iterator randomDrawIterator = randomDraw.iterator();
        while (randomDrawIterator.hasNext()) {
            Id<Person> toRemoveId = (Id<Person>) randomDrawIterator.next();
            log.println("Removing the person " + toRemoveId);
            population.removePerson(toRemoveId);
        }
        Controler controler = new Controler(scenario);
        controler.run();
    }

    public static List<Id<Person>> pickNRandom (List < Id < Person >> lst,double n){
                List<Id<Person>> copy = new LinkedList<Id<Person>>(lst);
                Collections.shuffle(copy);
                return copy.subList(0, (int) n);
            }
}