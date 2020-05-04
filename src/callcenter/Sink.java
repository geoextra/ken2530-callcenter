package callcenter;

import java.util.ArrayList;

/**
 * A sink
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

public class Sink implements ProductAcceptor {
    /**
     * All products are kept
     */
    private final ArrayList<Customer> customers;
    /**
     * All properties of products are kept
     */
    private final ArrayList<Integer> numbers;
    private final ArrayList<Double> times;
    private final ArrayList<String> events;
    private final ArrayList<String> stations;
    /**
     * Name of the sink
     */
    private final String name;
    /**
     * Counter to number products
     */
    private int number;

    /**
     * Constructor, creates objects
     */
    public Sink(String n) {
        name = n;
        customers = new ArrayList<>();
        numbers = new ArrayList<>();
        times = new ArrayList<>();
        events = new ArrayList<>();
        stations = new ArrayList<>();
        number = 0;
    }

    @Override
    public boolean giveCustomer(Customer p) {
        number++;
        customers.add(p);
        // store stamps
        ArrayList<Double> t = p.getTimes();
        ArrayList<String> e = p.getEvents();
        ArrayList<String> s = p.getStations();
        for (int i = 0; i < t.size(); i++) {
            numbers.add(number);
            times.add(t.get(i));
            events.add(e.get(i));
            stations.add(s.get(i));
        }
        return true;
    }

    public int[] getNumbers() {
        numbers.trimToSize();
        int[] tmp = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            tmp[i] = (numbers.get(i)).intValue();
        }
        return tmp;
    }

    public double[] getTimes() {
        times.trimToSize();
        double[] tmp = new double[times.size()];
        for (int i = 0; i < times.size(); i++) {
            tmp[i] = (times.get(i)).doubleValue();
        }
        return tmp;
    }

    public String[] getEvents() {
        String[] tmp = new String[events.size()];
        tmp = events.toArray(tmp);
        return tmp;
    }

    public String[] getStations() {
        String[] tmp = new String[stations.size()];
        tmp = stations.toArray(tmp);
        return tmp;
    }
}