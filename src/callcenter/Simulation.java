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


        double simulationTime = 20000;
        eventList.start(simulationTime); // 2000 is maximum time

        double[] waitingTimesConsumer = waitingTimes(consumerList);
        double[] waitingTimesCorporate = waitingTimes(corporateList);

        performanceCheck(consumerList, corporateList);
    }

    public static double[] waitingTimes(LinkedList<Customer> customerList) {
        double[] waitingTimes = new double[customerList.size()];

        System.out.print("[");
        for (int i = 0; i < customerList.size(); i++) {
            Customer consumer = customerList.get(i);

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
        return waitingTimes;
    }


    public static void performanceCheck(LinkedList<Customer> consumerList, LinkedList<Customer> corporateList) {
        double[] consumerWaitingTimes = waitingTimes(consumerList);
        double[] corporateWaitingTimes = waitingTimes(corporateList);

        double consumerThreshold1 = 5 * 60;
        double consumerThreshold2 = 10 * 60;

        double corporateThreshold1 = 3 * 60;
        double corporateThreshold2 = 7 * 60;

        System.out.println(satisfiedCustomers(consumerWaitingTimes, consumerThreshold1));
        System.out.println(satisfiedCustomers(consumerWaitingTimes, consumerThreshold2));
        System.out.println(satisfiedCustomers(corporateWaitingTimes, corporateThreshold1));
        System.out.println(satisfiedCustomers(corporateWaitingTimes, corporateThreshold2));
    }

    public static double satisfiedCustomers(double[] waitingTimesArray, double waitingTimeThreshold) {
        int customersBelowThreshold = 0;
        for (double customerWaitingTime: waitingTimesArray) {
            if (customerWaitingTime <= waitingTimeThreshold && customerWaitingTime != -1) customersBelowThreshold++;
        }

        return (double)customersBelowThreshold / (double)waitingTimesArray.length;
    }
}
