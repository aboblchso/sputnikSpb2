package PlanGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Passenger {
    String passengerId;
    List tripList;

    public Passenger(String passengerId) {
        this.passengerId = passengerId;
        this.tripList = new ArrayList();
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

}
