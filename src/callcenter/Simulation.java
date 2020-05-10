/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

package callcenter;

import java.util.LinkedList;
import java.util.Random;

public class Simulation {
    /**
     * @param args the command line arguments
     */

    public static final Random randomGenerator = new Random(42);

    public static void main(String[] args) {
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();
        Queue corporateQueue = new Queue();
        LinkedList<Customer> consumerSet = new LinkedList<>();
        LinkedList<Customer> corporateSet = new LinkedList<>();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", 30, false, consumerSet);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", 30, true, corporateSet);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");

        Agent consumerAgent1 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 1", false);
        Agent consumerAgent2 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 2", false);
        Agent consumerAgent3 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 3", false);
        Agent consumerAgent4 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 4", false);
        Agent consumerAgent5 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 5", false);

        Agent corporateAgent1 = new Agent(corporateQueue, corporateSink, eventList, "Corporate Agent 1", true);

        double simulationTime = 20000;
        eventList.start(simulationTime); // 2000 is maximum time

        System.out.print("[");
        for (Customer consumer : consumerSet) {
            double waitingTime;

            boolean pickedUp = consumer.getEvents().contains("Processing started");
            if (pickedUp) {
                double creationTime = consumer.getTimes().get(consumer.getEvents().indexOf("Creation"));
                double pickupTime = consumer.getTimes().get(consumer.getEvents().indexOf("Processing started"));
                waitingTime = pickupTime - creationTime;
            } else {
                waitingTime = -1;
            }

            System.out.print(waitingTime + ", ");
        }
        System.out.println("]");
    }
}
