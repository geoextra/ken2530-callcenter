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
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();
        Queue corporateQueue = new Queue();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", 30, false);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", 30, true);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");

        Agent consumerAgent1 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 1", false);
        Agent consumerAgent2 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 2", false);

        Agent corporateAgent1 = new Agent(corporateQueue, corporateSink, eventList, "Corporate Agent 1", true);

        eventList.start(2000); // 2000 is maximum time
    }
}
