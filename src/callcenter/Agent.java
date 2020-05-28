package callcenter;

import java.util.LinkedHashSet;
import java.util.Set;

import static callcenter.DateUtils.*;

/**
 * Agent in a factory
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class Agent implements CProcess, CustomerAcceptor {
    private final static Set<Agent> availableCorporateAgents = new LinkedHashSet<>();
    private static int k = 3;

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
    private final Queue consumerQueue;
    private final Queue corporateQueue;
    /**
     * Sink to dump customers
     */
    private final CustomerAcceptor sink;
    /**
     * Mean processing time
     */
    private final double consumerMeanProcTime = 72;
    private final double consumerDerivProcTime = 35;
    private final double consumerMinProcTime = 25;
    private final double corporateMeanProcTime = 216;
    private final double corporateDerivProcTime = 72;
    private final double corporateMinProcTime = 42;

    /**
     * Processing time iterator
     */
    private final boolean corporate;
    private final ShiftType shiftType;
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
     * @param conQ  Queue from which the agent has to take consumers
     * @param corpQ Queue from which the agent has to take corporates
     * @param s     Where to send the completed customers
     * @param e     Eventlist that will manage events
     * @param n     The name of the agent
     * @param c     Indicator if agent is corporate
     * @param t     shift the agent is belonging to
     */
    public Agent(Queue conQ, Queue corpQ, CustomerAcceptor s, CEventList e, String n, boolean c, ShiftType t) {
        busy = false;
        consumerQueue = conQ;
        corporateQueue = corpQ;
        sink = s;
        eventlist = e;
        name = n;

        corporate = c;
        shiftType = t;

        becomeIdle();
    }

    public static double drawRandomTruncatedNormal(double mean, double variance, double min) {
        double number = Simulation.randomGenerator.nextGaussian() * Math.sqrt(variance) + mean;
        if (number >= min) {
            return number;
        } else {
            return drawRandomTruncatedNormal(mean, variance, min);
        }
    }

    private void becomeIdle() {
        // set agent status to idle
        busy = false;

        if (isCorporate()) {
            availableCorporateAgents.add(this);

            // Ask the queue for corporate customers
            corporateQueue.askCustomer(this);
            if (!busy && availableCorporateAgents.size() > k) consumerQueue.askCustomer(this);
        } else {
            // Ask the queue for consumer customers
            consumerQueue.askCustomer(this);
        }
    }

    public boolean isCorporate() {
        return corporate;
    }

    /**
     * Method to have this object execute an event
     *
     * @param type The type of the event that has to be executed
     * @param time The current time
     */
    public void execute(EventType type, double time) {
        if (type == EventType.CUSTOMER_FINISHED && customer != null) {
            // show arrival
            System.out.println("Customer finished at time = " + time);
            // Remove customer from system
            customer.stamp(time, "Processing finished", name);
            sink.giveCustomer(customer);
            customer = null;
        }

        becomeIdle();
    }

    private boolean timeInShift(double time) {
        double hourOfDay = hourOfDay(time);
        return (((shiftType == ShiftType.MORNING) && (6 <= hourOfDay) && (hourOfDay < 14)) ||
                ((shiftType == ShiftType.AFTERNOON) && (14 <= hourOfDay) && (hourOfDay < 22)) ||
                ((shiftType == ShiftType.NIGHT) && (22 <= hourOfDay || hourOfDay < 6)) ||
                (shiftType == ShiftType.DKE_STAFF_MEMBER));
    }

    private double nextShiftStart(double time) {
        double hourOfDay = hourOfDay(time);
        int startingHour = 0;
        switch (shiftType) {
            case MORNING:
                startingHour = 6;
                break;
            case AFTERNOON:
                startingHour = 14;
                break;
            case NIGHT:
                startingHour = 22;
                break;
        }

        if (hourOfDay > startingHour) {
            return startOfNextDayInSeconds(time) + hoursToSeconds(startingHour);
        } else {
            return startOfDayInSeconds(time) + hoursToSeconds(startingHour);
        }
    }

    /**
     * Let the agent accept a customer and let it start handling it
     *
     * @param c The customer that is offered
     * @return true if the customer is accepted and started, false in all other cases
     */
    @Override
    public boolean giveCustomer(Customer c) {
        double time = eventlist.getTime();
        boolean inShift = timeInShift(time);
        // Only accept something if the agent is idle
        if (!busy && inShift) {
            if (isCorporate()) availableCorporateAgents.remove(this);

            // accept the customer
            customer = c;
            // mark starting time
            customer.stamp(time, "Processing started", name);
            // start production
            startProduction();
            // Flag that the customer has arrived
            return true;
        } else if (!inShift) {
            // create activation call event and queue it
            eventlist.add(this, EventType.SHIFT_START, nextShiftStart(time));
            return false;
        }
        // Flag that the customer has been rejected
        else return false;
    }

    /**
     * Starting routine for the production
     * Start the handling of the current customer with an truncated normal distributed processing time
     * This time is placed in the eventlist
     */
    private void startProduction() {
        double duration;
        if (customer.isCorporate()) {
            duration = drawRandomTruncatedNormal(corporateMeanProcTime, Math.pow(corporateDerivProcTime, 2), corporateMinProcTime);
        } else {
            duration = drawRandomTruncatedNormal(consumerMeanProcTime, Math.pow(consumerDerivProcTime, 2), consumerMinProcTime);
        }
        // Create a new event in the eventlist
        double time = eventlist.getTime();
        eventlist.add(this, EventType.CUSTOMER_FINISHED, time + duration); //target,type,time
        // set status to busy
        busy = true;
    }

    public int getHourlyCost() {
        return corporate ? 60 : 35;
    }

    public int getDailyCost() {
        return shiftType == ShiftType.DKE_STAFF_MEMBER ? getHourlyCost() * 24 : getHourlyCost() * 8;
    }
}