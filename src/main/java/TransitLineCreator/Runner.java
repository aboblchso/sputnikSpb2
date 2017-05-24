package TransitLineCreator;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.vehicles.VehicleWriterV1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ekaterina on 04.09.2016.
 */
public class Runner {
    public static void main(String[] args) throws IOException {
        Map<String , List<String>> nodeIds = new HashMap<>();
        Map<String , List<String>> linksIds = new HashMap<>();
        Map<String , Integer> openingHour = new HashMap<>();
        Map<String , Integer> closingHour = new HashMap<>();
        Map<String , Integer> frequency = new HashMap<>();
        Map<String, String> mode = new HashMap<>();

        Config config = ConfigUtils.createConfig();
        config.transit().setUseTransit(true);
        Scenario scenario = ScenarioUtils.createScenario(config);
        MatsimNetworkReader matsimNetworkReader = new MatsimNetworkReader(scenario.getNetwork());
        matsimNetworkReader.readFile("input/network.xml");

        Files.readAllLines(Paths.get("input/links.csv")).stream().forEach(line->{
            String[] items = line.split(";");
            String routeID = items[0];
            Integer opening = Integer.parseInt(items[3]);
            Integer closing = Integer.parseInt(items[4]);
            Integer freq = Integer.parseInt(items[5]);
            String mod = items[6];

            List<String> list = linksIds.get(routeID);
            if (list == null){
                list = new ArrayList<>();
                linksIds.put(routeID, list);
            }
            list.add(items[2]);
            openingHour.put(routeID, opening);
            closingHour.put(routeID, closing);
            frequency.put(routeID, freq);
            mode.put(routeID, mod);
        });

        Files.readAllLines(Paths.get("input/nodes.csv")).stream().forEach(line->{
            String[] items = line.split(";");
            String routeID = items[0];
            List<String> list = nodeIds.get(routeID);
            if (list == null){
                list = new ArrayList<>();
                nodeIds.put(routeID, list);
            }
            list.add(items[2]);
        });

        linksIds.forEach((id, links)->{
            //if (id.startsWith("7_2")) return;
            List<String> nodes = nodeIds.get(id);
            String[] nodesArray = new String[nodes.size()];
            nodesArray = nodes.toArray(nodesArray);
            String[] linksArray = new String[links.size()];
            linksArray = links.toArray(linksArray);
            Integer open = openingHour.get(id);
            Integer close = closingHour.get(id);
            Integer fr = frequency.get(id);
            String md = mode.get(id);
            LineCreator lineCreator = new LineCreator(scenario.getNetwork(), id,nodesArray, linksArray, open, close, fr, md, scenario);
            lineCreator.create();
        });
        TransitScheduleWriter transitScheduleWriter = new TransitScheduleWriter(scenario.getTransitSchedule());
        transitScheduleWriter.writeFile("transitSchedule.xml");
        VehicleWriterV1 vehicleWriterV1 = new VehicleWriterV1(scenario.getTransitVehicles());
        vehicleWriterV1.writeFile("transitVehicles.xml");
        NetworkWriter networkWriter = new NetworkWriter(scenario.getNetwork());
        networkWriter.write("network.xml");

        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        Controler controler = new Controler(scenario);

        controler.run();

    }
}
