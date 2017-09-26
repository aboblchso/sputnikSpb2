/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2016 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.contrib.av.robotaxi.run;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.av.robotaxi.scoring.*;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.contrib.taxi.run.*;
import org.matsim.core.config.*;
import org.matsim.core.controler.*;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;
import ru.otslab.sputnikCalculator.AgentsTripModeModifier;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class runs an example robotaxi scenario including scoring. The
 * simulation runs for 10 iterations, this takes quite a bit time (25 minutes or
 * so). You may switch on OTFVis visualisation in the main method below.
 * The scenario should run out of the box without any additional files.
 * If required, you may find all input files in the resource path
 * or in the jar maven has downloaded).
 * There are two vehicle files: 2000 vehicles and 5000, which may be set in the config.
 * Different fleet sizes can be created using {@link org.matsim.contrib.robotaxi.vehicles.CreateTaxiVehicles}
 *
 *
 */
public class RunRobotaxiExample {

    private static final double POPULATION_SAMPLE = 0.01;
    private static boolean SCALE_POPULATION = true;

    public static void main(String[] args) {
        String configFile = "config_horizon_2021_1_Robotaxi.xml";

        RunRobotaxiExample.run(configFile, true);
    }

    public static void run(String configFile, boolean otfvis) {
        Config config = ConfigUtils.loadConfig(configFile, new DvrpConfigGroup(), new TaxiConfigGroup(),
                new OTFVisConfigGroup(), new TaxiFareConfigGroup());
        createControler(config, otfvis).run();
    }

    public static Controler createControler(Config config, boolean otfvis) {
        config.addConfigConsistencyChecker(new TaxiConfigConsistencyChecker());
        config.checkConsistency();

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Population population = scenario.getPopulation();
        scaleDownPopulation(population);
        removeRandomDrawOfAgents(POPULATION_SAMPLE, population);

        AgentsTripModeModifier modeModifier = new AgentsTripModeModifier(population);
        modeModifier.clean("pt");
        modeModifier.changeMode("car","taxi");


        Controler controler = new Controler(scenario);
        controler.addOverridingModule(new AbstractModule() {
            @Override
            public void install() {
                addEventHandlerBinding().to(TaxiFareHandler.class).asEagerSingleton();
            }
        });
        controler.addOverridingModule(new TaxiOutputModule());
        controler.addOverridingModule(new TaxiModule());

        if (otfvis) {
            controler.addOverridingModule(new OTFVisLiveModule());
        }

        return controler;
    }

    private static void scaleDownPopulation(Population population) {
        if (SCALE_POPULATION){
            removeRandomDrawOfAgents(POPULATION_SAMPLE, population);
        }
    }

    private static List<Id<Person>> getPersonIds(Population population) {
        List<Id<Person>> personIdList2 = new LinkedList<Id<Person>>();
        Iterator personIterator = population.getPersons().values().iterator();
        while (personIterator.hasNext()) {
            Person person = (Person) personIterator.next();
            personIdList2.add(person.getId());
        }
        return personIdList2;
    }

    private static void removeRandomDrawOfAgents(double populationSample, Population population) {
        List<Id<Person>> personIdList2 = getPersonIds(population);
        List<Id<Person>> randomDraw = pickNRandom(personIdList2, personIdList2.size() * (1 - populationSample));
        Iterator randomDrawIterator = randomDraw.iterator();
        while (randomDrawIterator.hasNext()) {
            Id<Person> toRemoveId = (Id<Person>) randomDrawIterator.next();
            System.out.println("Removing the person " + toRemoveId);
            population.removePerson(toRemoveId);
        }
    }

    public static List<Id<Person>> pickNRandom (List < Id < Person >> lst, double n){
        List<Id<Person>> copy = new LinkedList<Id<Person>>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, (int) n);
    }
}