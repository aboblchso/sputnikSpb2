package ru.otslab.sputnikCalculator;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;

import java.util.ArrayList;

/**
 * Created by volot on 17.05.2017.
 */
public class NetworkCleaner {
    private final String ending;


    private final Network network;

    public NetworkCleaner(String ending, Network network) {
        this.ending = ending;
        this.network = network;
    }

    public void clean() {
        for (Link link : new ArrayList<Link>(network.getLinks().values())) {
            if (isFalseLink(link)) {
                network.removeLink(link.getId());
            }
        }
    }

    private boolean isFalseLink(Link link) {
        //List<? extends > personPlans = person.getPlans();
        if (link.getId().toString() == ending)
            return true;
        return false;
    }
}
