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
import org.matsim.contrib.accessibility.utils.AccessibilityUtils;
import org.matsim.contrib.accessibility.utils.NetworkUtil;
import org.matsim.contrib.minibus.PConfigGroup;
import org.matsim.contrib.minibus.hook.PModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacilitiesImpl;
import org.matsim.facilities.ActivityOption;
import org.matsim.facilities.ActivityOptionImpl;

import java.util.*;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by volot on 12.05.2017.
 */
public class RunnerCaOnValidations {
    public static void main(String[] args) {
        double scaleCoefficient = 0.05;
        double populationSample = 0.1;
        boolean scalePopulation = true;
        boolean removePersonOnMode = true;
        String configFile = "config_horizon_2021_1_car_student_bir.xml";
        Config config = ConfigUtils.loadConfig(configFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        //config.global().setNumberOfThreads(12);
        config.qsim().setFlowCapFactor(scaleCoefficient);
        config.qsim().setStorageCapFactor(scaleCoefficient * 2);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
 /*
        AccessibilityConfigGroup accConfig = ConfigUtils.addOrGetModule(config, AccessibilityConfigGroup.class);
        accConfig.setComputingAccessibilityForMode(Modes4Accessibility.freespeed, true);
        accConfig.setComputingAccessibilityForMode(Modes4Accessibility.car, true);

        accConfig.setCellSizeCellBasedAccessibility(500);



        double[] boundingBox = NetworkUtils.getBoundingBox(scenario.getNetwork().getNodes().values());
        accConfig.setBoundingBoxLeft(boundingBox[0]);
        accConfig.setBoundingBoxRight(boundingBox[1]);
        accConfig.setBoundingBoxTop(boundingBox[2]);
        accConfig.setBoundingBoxBottom(boundingBox[3]);


        Population population = scenario.getPopulation();
        List<Id<Person>> personIdList = new LinkedList<Id<Person>>();
        changePtToCar(population);


        //Population drawedPopulation = PopulationUtils.createPopulation(config);
        List<Id<Person>> personIdList2 = new LinkedList<Id<Person>>();

        cleanIncorrectPlans(population, personIdList2);
        drawPopulationSample(populationSample, population, personIdList2);



        List<String> activityTypes = new ArrayList<>();
        ActivityOption activityOption = new ActivityOptionImpl("pharmacy");
        activityTypes.add(activityOption.getType());
        log.println( "found the following activity types: " + activityTypes );

*/

        Controler controler = new Controler(scenario);
 /*
        for (final String actType : activityTypes) { // add an overriding module per activity type:
            final AccessibilityModule module = new AccessibilityModule();
            module.setConsideredActivityType(actType);
            controler.addOverridingModule(module);
        }
*/
        controler.run();
    }

    private static void drawPopulationSample(double populationSample, Population population, List<Id<Person>> personIdList2) {
        List<Id<Person>> randomDraw = pickNRandom(personIdList2, personIdList2.size() * (1 - populationSample));
        Iterator randomDrawIterator = randomDraw.iterator();
        while (randomDrawIterator.hasNext()) {
            Id<Person> toRemoveId = (Id<Person>) randomDrawIterator.next();
            log.println("Removing the person " + toRemoveId);
            population.removePerson(toRemoveId);
        }
    }

    private static void cleanIncorrectPlans(Population population, List<Id<Person>> personIdList2) {
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
    }

    private static void changePtToCar(Population population) {
        AgentsTripModeModifier modeModifier = new AgentsTripModeModifier(population);
        modeModifier.changeMode("pt","car");
    }

    public static List<Id<Person>> pickNRandom (List < Id < Person >> lst,double n){
                List<Id<Person>> copy = new LinkedList<Id<Person>>(lst);
                Collections.shuffle(copy);
                return copy.subList(0, (int) n);
            }
}