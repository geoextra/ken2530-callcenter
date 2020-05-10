/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

package callcenter;

import java.util.HashSet;
import java.util.Set;

public class Simulation {
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();
        Queue corporateQueue = new Queue();
        Set<Customer> consumerSet = new HashSet<>();
        Set<Customer> corporateSet = new HashSet<>();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", 30, false, consumerSet);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", 30, true, corporateSet);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");

        Agent consumerAgent1 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 1", false);
        Agent consumerAgent2 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 2", false);

        Agent corporateAgent1 = new Agent(corporateQueue, corporateSink, eventList, "Corporate Agent 1", true);

        double simulationTime = 20000;
        eventList.start(simulationTime); // 2000 is maximum time

        System.out.print("[");
        for (Customer consumer : consumerSet) {
            double creationTime = 0;
            double pickupTime = simulationTime;
            for (String eventName : consumer.getEvents()) {
                if (eventName.equals("Creation")) {
                    creationTime = consumer.getTimes().get(consumer.getEvents().indexOf(eventName));
                } else if (eventName.equals("Processing started")) {
                    pickupTime = consumer.getTimes().get(consumer.getEvents().indexOf(eventName));
                }
            }
            double waitingTime = pickupTime - creationTime;
            System.out.print(waitingTime + ", ");
        }
        System.out.println("]");
    }
}
