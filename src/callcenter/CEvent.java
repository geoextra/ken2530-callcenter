package callcenter;

/**
 * Event class
 * Events that facilitate changes in the simulation
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class CEvent {
    /**
     * The object involved with the event
     */
    private final CProcess target;
    /**
     * The type of the event
     */
    private final int type;
    /**
     * The time on which the event will be executed
     */
    private final double executionTime;

    /**
     * Constructor for objects
     *
     * @param dl  The object that will process the event
     * @param tp  The type of the event
     * @param time The time on which the event will be executed
     */
    public CEvent(CProcess dl, int tp, double time) {
        target = dl;
        type = tp;
        executionTime = time;
    }

    /**
     * Method to signal the target to execute an event
     */
    public void execute() {
        target.execute(type, executionTime);
    }

    /**
     * Method to ask the event at which time it will be executed
     *
     * @return The time at which the event will be executed
     */
    public double getExecutionTime() {
        return executionTime;
    }
}
