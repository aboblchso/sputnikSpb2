package ru.otslab.sputnikCalculator;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.contrib.accessibility.AccessibilityConfigGroup;
import org.matsim.contrib.accessibility.AccessibilityModule;
import org.matsim.contrib.accessibility.Modes4Accessibility;
import org.matsim.contrib.minibus.PConfigGroup;
import org.matsim.contrib.minibus.hook.PModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by volot on 12.05.2017.
 */
public class RunnerPtWithAccessibility {
    public static void main(String[] args) {
        double scaleCoefficient = 10.0;
        double populationSample = 0.05;
        boolean scalePopulation = true;
        boolean removePersonOnMode = true;
        String configFile = "config_horizon_2021_1_pt_access.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        PConfigGroup pConfig = (PConfigGroup)ConfigUtils.addOrGetModule(config, PConfigGroup.class);
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

        Iterator personIterator = new ArrayList<>(population.getPersons().values()).iterator();
        while (personIterator.hasNext()) {
            Person person = (Person) personIterator.next();
            if (person.getPlans().isEmpty() || person.getPlans().get(0).getPlanElements().isEmpty()){
                population.removePerson(person.getId());
            } else {
                personIdList2.add(person.getId());

                List<PlanElement> elements = person.getPlans().get(0).getPlanElements();
                PlanElement lstElement = elements.get((elements.size() - 1));
                if (lstElement instanceof Leg){
                    elements.remove(lstElement);
                }
            }
            /*
                Id<Person> toAddId = (Id<Person>) randomDrawIterator.next();
                Person toAddPerson = population.getPersons(Map<Id<Person>, args> );
                drawedPopulation.addPerson(toRemoveId);
                */
            }
            List<Id<Person>> randomDraw = pickNRandom(personIdList2, personIdList2.size() * (1 - populationSample));
            Iterator randomDrawIterator = randomDraw.iterator();
            while (randomDrawIterator.hasNext()) {
                Id<Person> toRemoveId = (Id<Person>) randomDrawIterator.next();
                log.println("Removing the person " + toRemoveId);
                population.removePerson(toRemoveId);
        }
        final AccessibilityConfigGroup acg = new AccessibilityConfigGroup();
        acg.setCellSizeCellBasedAccessibility(100);
        acg.setAreaOfAccessibilityComputation(AccessibilityConfigGroup.AreaOfAccesssibilityComputation.fromBoundingBox);
        acg.setBoundingBoxBottom(6634000);
        acg.setBoundingBoxTop(6667000);
        acg.setBoundingBoxLeft(671000);
        acg.setBoundingBoxRight(697000);
        acg.setTimeOfDay((double) (8 * 3600));
        acg.setComputingAccessibilityForMode(Modes4Accessibility.pt, true);
        acg.setComputingAccessibilityForMode(Modes4Accessibility.freespeed, true);
        config.addModule(acg);

        AccessibilityModule module = new AccessibilityModule();

        Controler controler = new Controler(scenario);
        controler.addOverridingModule(module);

        controler.run();
    }
            public static List<Id<Person>> pickNRandom (List < Id < Person >> lst,double n){
                List<Id<Person>> copy = new LinkedList<Id<Person>>(lst);
                Collections.shuffle(copy);
                return copy.subList(0, (int) n);
            }
}