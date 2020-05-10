package callcenter;

import java.util.ArrayList;

/**
 * Queue that stores customers until they can be handled on a machine machine
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Queue implements CustomerAcceptor {
    /**
     * List in which the customers are kept
     */
    private final ArrayList<Customer> row;
    /**
     * Requests from machine that will be handling the customers
     */
    private final ArrayList<Agent> requests;

    /**
     * Initializes the queue and introduces a dummy machine
     * the machine has to be specified later
     */
    public Queue() {
        row = new ArrayList<>();
        requests = new ArrayList<>();
    }

    /**
     * Asks a queue to give a customer to a machine
     * True is returned if a customer could be delivered; false if the request is queued
     */
    public boolean askCustomer(Agent agent) {
        // This is only possible with a non-empty queue
        if (row.size() > 0) {
            // If the machine accepts the customer
            if (agent.giveCustomer(row.get(0))) {
                row.remove(0);// Remove it from the queue
                return true;
            } else
                return false; // Machine rejected; don't queue request
        } else {
            requests.add(agent);
            return false; // queue request
        }
    }

    /**
     * Offer a customer to the queue
     * It is investigated whether a machine wants the customer, otherwise it is stored
     */
    public boolean giveCustomer(Customer p) {
        // Check if the machine accepts it
        if (requests.size() < 1)
            row.add(p); // Otherwise store it
        else {
            boolean delivered = false;
            while (!delivered & (requests.size() > 0)) {
                delivered = requests.get(0).giveCustomer(p);
                // remove the request regardless of whether or not the customer has been accepted
                requests.remove(0);
            }
            if (!delivered)
                row.add(p); // Otherwise store it
        }
        return true;
    }
}