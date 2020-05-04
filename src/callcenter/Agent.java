package callcenter;

import java.util.Random;

/**
 * Machine in a factory
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Agent implements CProcess, ProductAcceptor {
    private static final Random randomGenerator = new Random();

    /**
     * Eventlist that will manage events
     */
    private final CEventList eventlist;
    /**
     * Machine name
     */
    private final String name;
    /**
     * Queue from which the machine has to take products
     */
    private final Queue queue;
    /**
     * Sink to dump products
     */
    private final ProductAcceptor sink;
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
     * Product that is being handled
     */
    private Customer customer;
    /**
     * Status of the machine
     */
    private boolean busy;
    /**
     * Processing times (in case pre-specified)
     */
    private double[] processingTimes;

    /**
     * Constructor
     * Service times are exponentially distributed with mean 30
     *
     * @param q Queue from which the machine has to take products
     * @param s Where to send the completed products
     * @param e Eventlist that will manage events
     * @param n The name of the machine
     * @param c Indicator if agent is corporate
     */
    public Agent(Queue q, ProductAcceptor s, CEventList e, String n, boolean c) {
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
        double number = Agent.randomGenerator.nextGaussian() * Math.sqrt(variance) + mean;
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
        System.out.println("Product finished at time = " + tme);
        // Remove product from system
        customer.stamp(tme, "Production complete", name);
        sink.giveCustomer(customer);
        customer = null;
        // set machine status to idle
        busy = false;
        // Ask the queue for products
        queue.askCustomer(this);
    }

    /**
     * Let the machine accept a customer and let it start handling it
     *
     * @param c The customer that is offered
     * @return true if the product is accepted and started, false in all other cases
     */
    @Override
    public boolean giveCustomer(Customer c) {
        // Only accept something if the machine is idle
        if (!busy && isCorporate() == c.isCorporate()) {
            // accept the product
            customer = c;
            // mark starting time
            customer.stamp(eventlist.getTime(), "Production started", name);
            // start production
            startProduction();
            // Flag that the product has arrived
            return true;
        }
        // Flag that the product has been rejected
        else return false;
    }

    /**
     * Starting routine for the production
     * Start the handling of the current product with an exponentionally distributed processingtime with average 30
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