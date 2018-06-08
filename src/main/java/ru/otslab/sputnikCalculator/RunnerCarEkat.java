package ru.otslab.sputnikCalculator;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
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
public class RunnerCarEkat {
    public static void main(String[] args) {
        double scaleCoefficient = 0.1;
        double populationSample = 1.0;
        boolean scalePopulation = true;
        boolean removePersonOnMode = true;
        String configFile = "config_horizon_2021_1_notaxi_car.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        //config.global().setNumberOfThreads(12);

        config.qsim().setFlowCapFactor(scaleCoefficient);
        config.qsim().setStorageCapFactor(scaleCoefficient );
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();



            AgentsTripModeModifier modeModifier = new AgentsTripModeModifier(population);
            modeModifier.changeMode("taxi","car");





        Controler controler = new Controler(scenario);
        controler.run();
    }
            public static List<Id<Person>> pickNRandom (List < Id < Person >> lst,double n){
                List<Id<Person>> copy = new LinkedList<Id<Person>>(lst);
                Collections.shuffle(copy);
                return copy.subList(0, (int) n);
            }
}