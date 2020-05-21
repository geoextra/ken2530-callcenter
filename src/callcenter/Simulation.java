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
        LinkedList<Customer> consumerList = new LinkedList<>();
        LinkedList<Customer> corporateList = new LinkedList<>();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", 30, false, consumerList);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", 30, true, corporateList);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");

        Agent consumerAgent1 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 1", false, ShiftType.MORNING);
        Agent consumerAgent2 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 2", false, ShiftType.AFTERNOON);
        Agent consumerAgent3 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 3", false, ShiftType.NIGHT);
        Agent consumerAgent4 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 4", false, ShiftType.SLAVE);
        Agent consumerAgent5 = new Agent(consumerQueue, consumerSink, eventList, "Consumer Agent 5", false, ShiftType.MORNING);

        Agent corporateAgent1 = new Agent(corporateQueue, corporateSink, eventList, "Corporate Agent 1", true, ShiftType.AFTERNOON);

        double simulationTime = 20000;
        eventList.start(simulationTime); // 2000 is maximum time

        double[] waitingTimes = new double[consumerList.size()];

        System.out.print("[");
        for (int i = 0; i < consumerList.size(); i++) {
            Customer consumer = consumerList.get(i);

            double waitingTime;

            boolean pickedUp = consumer.getEvents().contains("Processing started");
            if (pickedUp) {
                double creationTime = consumer.getTimes().get(consumer.getEvents().indexOf("Creation"));
                double pickupTime = consumer.getTimes().get(consumer.getEvents().indexOf("Processing started"));
                waitingTime = pickupTime - creationTime;
            } else {
                waitingTime = -1;
            }

            waitingTimes[i] = waitingTime;

                    System.out.print(waitingTime + ", ");
        }
        System.out.println("]");


    }
}
