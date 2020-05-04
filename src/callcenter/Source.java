package callcenter;

/**
 * A source of products
 * This class implements CProcess so that it can execute events.
 * By continuously creating new events, the source keeps busy.
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Source implements CProcess {
    /**
     * Eventlist that will be requested to construct events
     */
    private final CEventList list;
    /**
     * Queue that buffers products for the machine
     */
    private final ProductAcceptor queue;
    /**
     * Name of the source
     */
    private final String name;
    /**
     * Mean interarrival time
     */
    private final double lambda;

    private final boolean generateCoporateCostumers;
    private double previousArrivalTime = 0;

    /**
     * Constructor, creates objects
     * Interarrival times are exponentially distributed with specified mean
     *
     * @param q The receiver of the products
     * @param l The eventlist that is requested to construct events
     * @param n Name of object
     * @param m Mean arrival time
     * @param c corporate
     */
    public Source(ProductAcceptor q, CEventList l, String n, double m, boolean c) {
        list = l;
        queue = q;
        name = n;
        lambda = m;
        generateCoporateCostumers = c;
        // put first event in list for initialization
        list.add(this, 0, drawRandomArrivalTime(lambda)); //target,type,time
    }

    // Lewis and Shedler (1979)
    public double drawRandomArrivalTime(double lambda) {
        double nextArrivalTime = previousArrivalTime;


        double u1 = Math.random();
        double u2 = Math.random();

        nextArrivalTime = nextArrivalTime - (1 / lambda) * Math.log(u1);

        if (u2 <= lambda / lambda) {
            return nextArrivalTime;
        } else {
            return drawRandomArrivalTime(lambda);
        }
    }

    @Override
    public void execute(int type, double tme) {
        // show arrival
        System.out.println("Arrival at time = " + tme);
        // give arrived product to queue
        Customer p = new Customer(generateCoporateCostumers);
        p.stamp(tme, "Creation", name);
        queue.giveCustomer(p);
        // generate duration
        // double duration = drawRandomExponential(meanArrTime);
        previousArrivalTime = tme;
        double duration = drawRandomArrivalTime(lambda);
        previousArrivalTime = duration;
        // Create a new event in the eventlist
        list.add(this, 0, tme + duration - tme); //target,type,time
    }
}