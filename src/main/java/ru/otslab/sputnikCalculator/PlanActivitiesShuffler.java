package ru.otslab.sputnikCalculator;

import com.sun.org.apache.bcel.internal.generic.POP;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;

import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import sun.security.util.Cache;

import java.util.*;

public class PlanActivitiesShuffler {

    public static void main(String[] args) {

        Config config = ConfigUtils.loadConfig("config_horizon_2021_1_car.xml");
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();
        shuffleActivities(population);
        writePopulation(population);

    }



    private static void shuffleActivities(Population population){

        // God forbid me for this shitcode Tim'Sept17

        Iterator iterator = population.getPersons().values().iterator();
        while (iterator.hasNext()){
            Person person = (Person) iterator.next();
            List<? extends Plan> personPlans = person.getPlans();
            for (PlanElement planElement : personPlans.get(personPlans.size() - 1).getPlanElements()) {
                if (planElement instanceof Activity) {
                    Activity activity = (Activity) planElement;
                    Double activityEndTime = activity.getEndTime();

                    if (activityEndTime >= 0) {
                        Random random = new Random();
                        Double randomDouble = random.nextDouble();
                        if ((activityEndTime > 0) & (activityEndTime <= 28800)) {
                            setHome(activity);
                        } else if ((activityEndTime > 28800) & (activityEndTime <= 36000)) {
                            if ((randomDouble > 0) & (randomDouble < 0.9)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.9) & (randomDouble <= 1)) {
                                setLeasure(activity);
                            } else if ((activityEndTime > 36000) & (activityEndTime <= 57600)) {
                                if ((randomDouble > 0) & (randomDouble < 0.2)) {
                                    setHome(activity);
                                } else if ((randomDouble > 0.2) & (randomDouble <= 0.4)) {
                                    setWork(activity);
                                } else if ((randomDouble > 0.4) & (randomDouble <= 0.7)) {
                                    setStudy(activity);
                                } else if ((randomDouble > 0.7) & (randomDouble <= 1)) {
                                    setLeasure(activity);
                                }
                            } else if ((activityEndTime > 57600) & (activityEndTime <= 64800)) {

                                if ((randomDouble > 0) & (randomDouble < 0.1)) {
                                    setHome(activity);
                                } else if ((randomDouble > 0.1) & (randomDouble <= 0.5)) {
                                    setWork(activity);
                                } else if ((randomDouble > 0.5) & (randomDouble <= 0.7)) {
                                    setStudy(activity);
                                } else if ((randomDouble > 0.7) & (randomDouble <= 1)) {
                                    setLeasure(activity);
                                }
                            } else if ((activityEndTime > 64800) & (activityEndTime <= 75600)) {
                                if ((randomDouble > 0) & (randomDouble < 0.1)) {
                                    setHome(activity);
                                } else if ((randomDouble > 0.1) & (randomDouble <= 0.7)) {
                                    setWork(activity);
                                } else if ((randomDouble > 0.7) & (randomDouble <= 0.8)) {
                                    setStudy(activity);
                                } else if ((randomDouble > 0.8) & (randomDouble <= 1)) {
                                    setLeasure(activity);
                                }
                            } else if ((activityEndTime > 75600) & (activityEndTime <= 86400)) {
                                if ((randomDouble > 0) & (randomDouble < 0.1)) {
                                    setHome(activity);
                                } else if ((randomDouble > 0.1) & (randomDouble <= 0.4)) {
                                    setWork(activity);
                                } else if ((randomDouble > 0.4) & (randomDouble <= 1)) {
                                    setLeasure(activity);
                                }
                            }
                        }
                    } else {
                        Double activityStartTime = activity.getStartTime();
                        Random random = new Random();
                        Double randomDouble = random.nextDouble();
                        if ((activityStartTime > 0) & (activityStartTime <= 18000)) {
                            setHome(activity);
                        } else if ((activityStartTime > 18000) & (activityStartTime <= 25200)) {
                            if ((randomDouble > 0) & (randomDouble < 0.5)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.5) & (randomDouble <= 1)) {
                                setWork(activity);
                            }
                        } else if ((activityStartTime > 25200) & (activityStartTime <= 32400)) {
                            if ((randomDouble > 0) & (randomDouble < 0.1)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.1) & (randomDouble <= 0.8)) {
                                setWork(activity);
                            } else if ((randomDouble > 0.8) & (randomDouble <= 1)) {
                                setStudy(activity);
                            }
                        }else if ((activityStartTime > 25200) & (activityStartTime <= 32400)){
                            if ((randomDouble > 0) & (randomDouble<0.2)) {
                                setHome(activity);
                                } else if ((randomDouble > 0.2) & (randomDouble<=0.4)){
                                    setWork(activity);
                                } else if ((randomDouble > 0.4) & (randomDouble<=0.7)){
                                    setStudy(activity);
                                } else if ((randomDouble > 0.7) & (randomDouble<=1)) {
                                setLeasure(activity);
                            }
                        } else if ((activityStartTime > 32400) & (activityStartTime <= 36000)){
                            if ((randomDouble > 0) & (randomDouble<0.1)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.1) & (randomDouble<=0.7)){
                                setWork(activity);
                            } else if ((randomDouble > 0.7) & (randomDouble<=1)) {
                                setStudy(activity);
                            }
                        } else if ((activityStartTime > 36000) & (activityStartTime <= 57600)){
                            if ((randomDouble > 0) & (randomDouble<0.1)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.1) & (randomDouble<=0.4)){
                                setWork(activity);
                            } else if ((randomDouble > 0.4) & (randomDouble<=0.8)){
                                setStudy(activity);
                            } else if ((randomDouble > 0.8) & (randomDouble<=1)) {
                                setLeasure(activity);
                            }
                        } else if ((activityStartTime > 57600) & (activityStartTime <= 72000)){
                            if ((randomDouble > 0) & (randomDouble<0.3)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.3) & (randomDouble<=0.4)){
                                setWork(activity);
                            } else if ((randomDouble > 0.4) & (randomDouble<=0.6)){
                                setStudy(activity);
                            } else if ((randomDouble > 0.6) & (randomDouble<=1)) {
                                setLeasure(activity);
                            }
                        } else if ((activityStartTime > 72000)) {
                            if ((randomDouble > 0) & (randomDouble < 0.7)) {
                                setHome(activity);
                            } else if ((randomDouble > 0.7) & (randomDouble <= 1)) {
                                setLeasure(activity);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void setStudy(Activity activity) {
        activity.setType("e");
    }

    private static void setWork(Activity activity) {
        activity.setType("w");
    }


    private static void setLeasure(Activity activity) {
        activity.setType("l");
    }



    private static void setHome(Activity activity) {
        activity.setType("h");
    }


    private static void writePopulation(Population population){
        PopulationWriter populationWriter = new PopulationWriter(population);
        populationWriter.writeV6("shuffledPopulation.xml");
    }
}

