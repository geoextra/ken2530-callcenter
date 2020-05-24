package callcenter;

import java.util.ArrayList;

/**
 * Customer that is send trough the system
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
class Customer {
    private final boolean corporate;

    /**
     * Stamps for the customers
     */
    private final ArrayList<Double> times;
    private final ArrayList<String> events;
    private final ArrayList<String> stations;

    /**
     * Constructor for the customer
     * Mark the time at which it is created
     */
    public Customer(boolean corporate) {
        this.corporate = corporate;
        times = new ArrayList<>();
        events = new ArrayList<>();
        stations = new ArrayList<>();
    }

    public boolean isCorporate() {
        return corporate;
    }


    public void stamp(double time, String event, String station) {
        times.add(time);
        events.add(event);
        stations.add(station);
    }

    public ArrayList<Double> getTimes() {
        return times;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public ArrayList<String> getStations() {
        return stations;
    }

    public double[] getTimesAsArray() {
        times.trimToSize();
        double[] tmp = new double[times.size()];
        for (int i = 0; i < times.size(); i++) {
            tmp[i] = times.get(i);
        }
        return tmp;
    }

    public String[] getEventsAsArray() {
        String[] tmp = new String[events.size()];
        tmp = events.toArray(tmp);
        return tmp;
    }

    public String[] getStationsAsArray() {
        String[] tmp = new String[stations.size()];
        tmp = stations.toArray(tmp);
        return tmp;
    }
}