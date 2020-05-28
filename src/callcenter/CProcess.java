package callcenter;

/**
 * Blueprint for processes
 * Classes that implement this interface can process events
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public interface CProcess {
    /**
     * Method to have this object process an event
     *
     * @param type The type of the event that has to be executed
     * @param time The current time
     */
    void execute(EventType type, double time);
}
