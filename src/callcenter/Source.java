package callcenter;

import java.util.List;

import static callcenter.DateUtils.*;

/**
 * A source of customers
 * This class implements CProcess so that it can execute events.
 * By continuously creating new events, the source keeps busy.
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class Source implements CProcess {
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
    public double lambdaConsumer_max = 3.8 / 60;
    public double lambdaCorporate_max = 1.0 / 60;
    private double previousArrivalTime = 0;

    /**
     * Constructor, creates objects
     * Interarrival times are exponentially distributed with specified mean
     *
     * @param q The receiver of the customers
     * @param l The eventlist that is requested to construct events
     * @param n Name of object
     * @param m Mean arrival time
     * @param c corporate
     * @param s pointer to list of customers
     */
    public Source(CustomerAcceptor q, CEventList l, String n, double m, boolean c, List<Customer> s) {
        list = l;
        queue = q;
        name = n;
        generateCorporateCostumers = c;
        // put first event in list for initialization
        list.add(this, 0, drawRandomArrivalTime()); //target,type,time
        customerList = s;
    }

    // Lewis and Shedler (1979)
    public double drawRandomArrivalTime() {
        double nextArrivalTime = previousArrivalTime;

        double lambda_max = generateCorporateCostumers ? lambdaCorporate_max : lambdaConsumer_max;

        double u1 = Simulation.randomGenerator.nextDouble();
        double u2 = Simulation.randomGenerator.nextDouble();

        nextArrivalTime = nextArrivalTime - ((1 / lambda_max) * Math.log(u1));

        double numerator = generateCorporateCostumers ? lambdaCorporate(nextArrivalTime) : lambdaConsumer(nextArrivalTime);

        if (u2 <= numerator / lambda_max) {
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
    public void execute(int type, double time) {
        // show arrival
        System.out.println("Arrival at time = " + time);
        // give arrived customer to queue
        Customer p = new Customer(generateCorporateCostumers);
        customerList.add(p);
        p.stamp(time, "Creation", name);
        queue.giveCustomer(p);
        // generate duration
        // double duration = drawRandomExponential(meanArrTime);
        //previousArrivalTime = time;
        double duration = drawRandomArrivalTime();
        // previousArrivalTime = duration;
        // Create a new event in the eventlist
        list.add(this, 0, time + duration - time); //target,type,time
    }
}