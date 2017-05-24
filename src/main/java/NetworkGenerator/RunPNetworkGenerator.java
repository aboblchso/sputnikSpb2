package NetworkGenerator;

import org.matsim.analysis.CalcLinkStats;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.contrib.accessibility.utils.NetworkUtil;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.algorithms.NetworkCleaner;
import org.matsim.core.network.algorithms.NetworkTransform;
import org.matsim.core.network.io.NetworkReaderMatsimV1;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.core.utils.io.OsmNetworkReader;


/**
 * "P" has to do with "Potsdam" and "Z" with "Zurich", but P and Z are mostly used to show which classes belong together.
 */
public class RunPNetworkGenerator {

    public static void main(String[] args) {

		/*
		 * The input file name.
		 */
        String osm = "C:/Users/nano/IdeaProjects/ots/sputnikSpb2/input/inputForNetwork/network_all4_speed_fix.osm";
        String networkFile = "input/inputForNetwork/tryWithJOSM.xml";
        Config config = ConfigUtils.createConfig();
        config.network().setInputFile(networkFile);
        Scenario scenario = ScenarioUtils.createScenario(config);
        Network network = scenario.getNetwork();


		/*
		 * The coordinate system to use. OpenStreetMap uses WGS84, but for MATSim, we need a projection where distances
		 * are (roughly) euclidean distances in meters.
		 *
		 * UTM 33N is one such possibility (for parts of Europe, at least).
		 *
		 */
        CoordinateTransformation ct =
                TransformationFactory.getCoordinateTransformation(TransformationFactory.WGS84, "EPSG:32635");


		/*
		 * First, create a new Config and a new Scenario. One always has to do this when working with the MATSim
		 * data containers.
		 *

        Config config = ConfigUtils.createConfig();
        Scenario scenario = ScenarioUtils.createScenario(config);
         */

		/*
		 * Pick the Network from the Scenario for convenience.
		 */
        ;


        OsmNetworkReader onr = new OsmNetworkReader(network,ct);
        onr.parse(osm);


		/*
		 * Clean the Network. Cleaning means removing disconnected components, so that afterwards there is a route from every link
		 * to every other link. This may not be the case in the initial network converted from OpenStreetMap.
		 */
        new NetworkCleaner().run(network);

		/*
		 * Write the Network to a MATSim network file.
		 */


        new NetworkWriter(network).writeV1("./input/networkNew.xml");

    }

}
