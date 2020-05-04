/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

package callcenter;

public class Simulation {
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        // Create an eventlist
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();

        Source consumerSource = new Source(consumerQueue, eventList, "Source 1", 2, false);

        Sink consumerSink = new Sink("Sink 1");

        Agent consumerAgent1 = new Agent(consumerQueue, consumerSink, eventList, "Agent 1", false);
        Agent consumerAgent2 = new Agent(consumerQueue, consumerSink, eventList, "Agent 2", false);

        // start the eventlist
        eventList.start(2000); // 2000 is maximum time
    }
}
