package callcenter;

/**
 * Blueprint for accepting customers
 * Classes that implement this interface can accept customers
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public interface CustomerAcceptor {
    /**
     * Method to have this object process an event
     *
     * @param p The customer that is accepted
     * @return true if accepted
     */
    boolean giveCustomer(Customer p);
}
