package callcenter;

/**
 * Agent in a factory
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Agent implements CProcess, CustomerAcceptor {
    /**
     * Eventlist that will manage events
     */
    private final CEventList eventlist;
    /**
     * Agent name
     */
    private final String name;
    /**
     * Queue from which the agent has to take customers
     */
    private final Queue queue;
    /**
     * Sink to dump customers
     */
    private final CustomerAcceptor sink;
    /**
     * Mean processing time
     */
    private final double consumerMeanProcTime;
    private final double consumerDerivProcTime;
    private final double consumerMinProcTime;
    private final double corporateMeanProcTime;
    private final double corporateDerivProcTime;
    private final double corporateMinProcTime;
    /**
     * Processing time iterator
     */
    private final boolean corporate;
    /**
     * Customer that is being handled
     */
    private Customer customer;
    /**
     * Status of the agent
     */
    private boolean busy;

    /**
     * Constructor
     * Service times are exponentially distributed with mean 30
     *
     * @param q Queue from which the agent has to take customers
     * @param s Where to send the completed customers
     * @param e Eventlist that will manage events
     * @param n The name of the agent
     * @param c Indicator if agent is corporate
     */
    public Agent(Queue q, CustomerAcceptor s, CEventList e, String n, boolean c) {
        busy = false;
        queue = q;
        sink = s;
        eventlist = e;
        name = n;
        consumerMeanProcTime = 72;
        consumerDerivProcTime = 35;
        consumerMinProcTime = 25;

        corporateMeanProcTime = 216;
        corporateDerivProcTime = 72;
        corporateMinProcTime = 45;

        corporate = c;
        queue.askCustomer(this);
    }

    public static double drawRandomTrancatedNormal(double mean, double variance, double min) {
        double number = Simulation.randomGenerator.nextGaussian() * Math.sqrt(variance) + mean;
        if (number >= min) {
            return number;
        } else {
            return drawRandomTrancatedNormal(mean, variance, min);
        }
    }

    public boolean isCorporate() {
        return corporate;
    }

    /**
     * Method to have this object execute an event
     *
     * @param type The type of the event that has to be executed
     * @param tme  The current time
     */
    public void execute(int type, double tme) {
        // show arrival
        System.out.println("Customer finished at time = " + tme);
        // Remove customer from system
        customer.stamp(tme, "Processing finished", name);
        sink.giveCustomer(customer);
        customer = null;
        // set agent status to idle
        busy = false;
        // Ask the queue for customers
        queue.askCustomer(this);
    }

    /**
     * Let the agent accept a customer and let it start handling it
     *
     * @param c The customer that is offered
     * @return true if the customer is accepted and started, false in all other cases
     */
    @Override
    public boolean giveCustomer(Customer c) {
        // Only accept something if the agent is idle
        if (!busy && isCorporate() == c.isCorporate()) {
            // accept the customer
            customer = c;
            // mark starting time
            customer.stamp(eventlist.getTime(), "Processing started", name);
            // start production
            startProduction();
            // Flag that the customer has arrived
            return true;
        }
        // Flag that the customer has been rejected
        else return false;
    }

    /**
     * Starting routine for the production
     * Start the handling of the current customer with an exponentionally distributed processingtime with average 30
     * This time is placed in the eventlist
     */
    private void startProduction() {
        double duration;
        if (customer.isCorporate()) {
            duration = drawRandomTrancatedNormal(corporateMeanProcTime, Math.pow(corporateDerivProcTime, 2), corporateMinProcTime);
        } else {
            duration = drawRandomTrancatedNormal(consumerMeanProcTime, Math.pow(consumerDerivProcTime, 2), consumerMinProcTime);
        }
        // Create a new event in the eventlist
        double tme = eventlist.getTime();
        eventlist.add(this, 0, tme + duration); //target,type,time
        // set status to busy
        busy = true;
    }
}