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
public class AgentsOnModeRemover {
    private final String mode;
    private final Population population;

    public AgentsOnModeRemover(String mode, Population population) {
        this.mode = mode;
        this.population = population;
    }

    public void clean(){
        for (Person person : new ArrayList<Person>(population.getPersons().values())){
            if(isModeUser(person)){
                population.removePerson(person.getId());
            }
        }
    }
    private boolean isModeUser(Person person){
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
