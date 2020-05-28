package callcenter;

import java.util.List;

import static callcenter.DateUtils.hourOfDay;
import static callcenter.DateUtils.secondsToHours;

/**
 * A source of customers
 * This class implements CProcess so that it can execute events.
 * By continuously creating new events, the source keeps busy.
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class Source implements CProcess {
    public final double lambdaConsumer_max = 3.8 / 60;
    public final double lambdaCorporate_max = 1.0 / 60;
    /**
     * Eventlist that will be requested to construct events
     */
    private final CEventList list;
    /**
     * Queue that buffers customers for the agent
     */
    private final CustomerAcceptor queue;
    /**
     * Name of the source
     */
    private final String name;
    private final boolean generateCorporateCostumers;
    private final List<Customer> customerList;
    private double previousArrivalTime = 0;

    /**
     * Constructor, creates objects
     *
     * @param q The receiver of the customers
     * @param l The eventlist that is requested to construct events
     * @param n Name of object
     * @param c Boolean weather the generated customers should be of the corporate or consumer type
     * @param s Pointer to list of customers
     */
    public Source(CustomerAcceptor q, CEventList l, String n, boolean c, List<Customer> s) {
        list = l;
        queue = q;
        name = n;
        generateCorporateCostumers = c;
        // put first event in list for initialization
        list.add(this, EventType.CUSTOMER_ARRIVAL, drawRandomArrivalTime()); //target,type,time
        customerList = s;
    }

    // Lewis and Shedler (1979)
    public double drawRandomArrivalTime() {
        double nextArrivalTime = previousArrivalTime;

        double lambda_max = generateCorporateCostumers ? lambdaCorporate_max : lambdaConsumer_max;

        double u1 = Simulation.randomGenerator.nextDouble();
        double u2 = Simulation.randomGenerator.nextDouble();

        nextArrivalTime = nextArrivalTime - ((1 / lambda_max) * Math.log(u1));

        double lambda_value = generateCorporateCostumers ? lambdaCorporate(nextArrivalTime) : lambdaConsumer(nextArrivalTime);

        if (u2 <= lambda_value / lambda_max) {
            previousArrivalTime = nextArrivalTime;
            return nextArrivalTime;
        } else {
            return drawRandomArrivalTime();
        }
    }

    public double lambdaConsumer(double time) {
        double time_h = secondsToHours(time);
        double rate_m = Math.sin((2 * Math.PI) / 24.0 * (time_h - 9)) * 1.8 + 2;
        return rate_m / 60;
    }

    public double lambdaCorporate(double time) {
        double hourOfDay = hourOfDay(time);
        double rate_m;
        if (8 <= hourOfDay && hourOfDay <= 18) {
            rate_m = 1.0;
        } else {
            rate_m = 0.2;
        }
        return rate_m / 60;
    }

    @Override
    public void execute(EventType type, double time) {
        // show arrival
        if (Simulation.print) if (Simulation.print) System.out.println("Arrival at time = " + time);
        // give arrived customer to queue
        Customer p = new Customer(generateCorporateCostumers);
        customerList.add(p);
        p.stamp(time, "Creation", name);
        queue.giveCustomer(p);
        // generate nextArrivalTime
        double nextArrivalTime = drawRandomArrivalTime();
        // Create a new event in the eventlist
        list.add(this, EventType.CUSTOMER_ARRIVAL, nextArrivalTime); //target,type,time
    }
}