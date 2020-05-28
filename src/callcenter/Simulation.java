package callcenter;

import java.util.LinkedList;
import java.util.Random;

import static callcenter.DateUtils.daysToSeconds;
import static callcenter.DateUtils.minutesToSeconds;

/**
 * Example program for using eventlists
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class Simulation {
    public static final Random randomGenerator = new Random(42);


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();
        Queue corporateQueue = new Queue();
        LinkedList<Customer> consumerList = new LinkedList<>();
        LinkedList<Customer> corporateList = new LinkedList<>();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", false, consumerList);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", true, corporateList);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");

        // 3 shift * 2 agent types * max 20 agents * 10 parameter values
        for (int i = 0; i < 4; i++) {
            final boolean useSlaves = true;
            if (!useSlaves) {
                Agent consumerAgent1 = new Agent(consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 1", false, ShiftType.MORNING);
                Agent consumerAgent2 = new Agent(consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 2", false, ShiftType.AFTERNOON);
                Agent consumerAgent3 = new Agent(consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 3", false, ShiftType.NIGHT);

                Agent corporateAgent1 = new Agent(consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 1", true, ShiftType.MORNING);
                Agent corporateAgent2 = new Agent(consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 2", true, ShiftType.AFTERNOON);
                Agent corporateAgent3 = new Agent(consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 3", true, ShiftType.NIGHT);
            } else {
                Agent consumerAgent1 = new Agent(consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 1", false, ShiftType.DKE_STAFF_MEMBER);
                Agent corporateAgent1 = new Agent(consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 1", true, ShiftType.DKE_STAFF_MEMBER);
            }
        }
        double daysToSimulate = 1;
        double simulationTime = daysToSeconds(daysToSimulate);
        eventList.start(simulationTime);

        // double[] waitingTimesConsumer = waitingTimes(consumerList);
        // double[] waitingTimesCorporate = waitingTimes(corporateList);

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

        double consumerThreshold1 = minutesToSeconds(5);
        double consumerThreshold2 = minutesToSeconds(10);

        double corporateThreshold1 = minutesToSeconds(3);
        double corporateThreshold2 = minutesToSeconds(7);

        System.out.println("Consumer handled within 5 minutes : " + satisfiedCustomers(consumerWaitingTimes, consumerThreshold1));
        System.out.println("Consumer handled within 10 minutes : " + satisfiedCustomers(consumerWaitingTimes, consumerThreshold2));
        System.out.println("Corporates handled within 3 minutes : " + satisfiedCustomers(corporateWaitingTimes, corporateThreshold1));
        System.out.println("Corporates handled within 7 minutes : " + satisfiedCustomers(corporateWaitingTimes, corporateThreshold2));
    }

    public static double satisfiedCustomers(double[] waitingTimesArray, double waitingTimeThreshold) {
        int customersBelowThreshold = 0;
        for (double customerWaitingTime : waitingTimesArray) {
            if (customerWaitingTime <= waitingTimeThreshold && customerWaitingTime != -1) customersBelowThreshold++;
        }

        return (double) customersBelowThreshold / (double) waitingTimesArray.length;
    }
}
