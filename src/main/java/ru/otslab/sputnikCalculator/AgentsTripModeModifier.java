/* *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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

package ru.otslab.sputnikCalculator;

import org.matsim.api.core.v01.population.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaroslav on 21.01.2017.
 */
public class AgentsTripModeModifier {
    private final Population population;

    public AgentsTripModeModifier(Population population) {
        this.population = population;
    }

    public void clean(String oldMode){
        for (Person person : new ArrayList<Person>(population.getPersons().values())){
            if(isModeUser(person, oldMode)){
                population.removePerson(person.getId());
            }
        }
    }

    public void changeMode(String oldMode, String newMode) {
        for (Person person : new ArrayList<Person>(population.getPersons().values())) {
            List<? extends Plan> personPlans = person.getPlans();
            for (PlanElement planElement : personPlans.get(personPlans.size() - 1).getPlanElements()) {
                if (planElement instanceof Leg) {
                    if (((Leg) planElement).getMode().equals(oldMode)) {
                        ((Leg) planElement).setMode(newMode);
                    }
                }
            }
        }
    }

    private boolean isModeUser(Person person, String mode){
        List<? extends Plan> personPlans = person.getPlans();
        for (PlanElement planElement : personPlans.get(personPlans.size() - 1).getPlanElements()) {
            if (planElement instanceof Leg){
                if (((Leg) planElement).getMode().equals(mode)){
                    return true;
                }
            }
        }
        return false;
    }
}
