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
    public static final boolean print = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int runs = 100;
        double[] results1 = new double[runs];
        double[] results2 = new double[runs];
        double[] results3 = new double[runs];
        double[] results4 = new double[runs];
        double[] waitingTimesAverageConsumer = new double[runs];
        double[] waitingTimesAverageCorporate = new double[runs];

        for (int i = 0; i < runs; i++) {
            double[] conf = runSimulation();
            results1[i] = conf[0];
            results2[i] = conf[1];
            results3[i] = conf[2];
            results4[i] = conf[3];
            waitingTimesAverageConsumer[i] = conf[4];
            waitingTimesAverageCorporate[i] = conf[5];
        }
        printMatlabArray("results1", results1);
        printMatlabArray("results2", results2);
        printMatlabArray("results3", results3);
        printMatlabArray("results4", results4);
        printMatlabArray("waitingTimesAverageConsumer", waitingTimesAverageConsumer);
        printMatlabArray("waitingTimesAverageCorporate", waitingTimesAverageCorporate);
    }


    public static double[] runSimulation() {
        CEventList eventList = new CEventList();

        Queue consumerQueue = new Queue();
        Queue corporateQueue = new Queue();
        LinkedList<Customer> consumerList = new LinkedList<>();
        LinkedList<Customer> corporateList = new LinkedList<>();

        Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", false, consumerList);
        Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", true, corporateList);

        Sink consumerSink = new Sink("Consumer sink");
        Sink corporateSink = new Sink("Corporate sink");
        AgentSet agentSet = new AgentSet(2);

        // 3 shift * 2 agent types * max 20 agents * 10 parameter values
        for (int i = 0; i < 5; i++) {
            Agent consumerAgent1 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 1", false, ShiftType.MORNING);
            Agent consumerAgent2 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 2", false, ShiftType.AFTERNOON);
            Agent consumerAgent3 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent 3", false, ShiftType.NIGHT);

            Agent corporateAgent1 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 1", true, ShiftType.MORNING);
            Agent corporateAgent2 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 2", true, ShiftType.AFTERNOON);
            Agent corporateAgent3 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent 3", true, ShiftType.NIGHT);
        }
        double daysToSimulate = 1;
        double simulationTime = daysToSeconds(daysToSimulate);
        assert simulationTime == 86400;
        eventList.start(simulationTime);

        // double[] waitingTimesConsumer = waitingTimes(consumerList);
        // double[] waitingTimesCorporate = waitingTimes(corporateList);

        return performanceCheck(consumerList, corporateList);
    }

    public static double[] waitingTimes(LinkedList<Customer> customerList) {
        double[] waitingTimes = new double[customerList.size()];
        if (print) System.out.print("[");
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
            if (print) System.out.print(waitingTime + ", ");
        }
        if (print) System.out.println("]");
        return waitingTimes;
    }

    public static double waitingTimesAverage(LinkedList<Customer> customerList) {
        double[] waitingTimes = waitingTimes(customerList);
        double sum = 0;
        for (double waitingTime : waitingTimes) sum += waitingTime;
        return sum / waitingTimes.length;
    }


    public static double[] performanceCheck(LinkedList<Customer> consumerList, LinkedList<Customer> corporateList) {
        double[] consumerWaitingTimes = waitingTimes(consumerList);
        double[] corporateWaitingTimes = waitingTimes(corporateList);

        double consumerThreshold1 = minutesToSeconds(5);
        double consumerThreshold2 = minutesToSeconds(10);

        double corporateThreshold1 = minutesToSeconds(3);
        double corporateThreshold2 = minutesToSeconds(7);

        double r1 = satisfiedCustomers(consumerWaitingTimes, consumerThreshold1);
        double r2 = satisfiedCustomers(consumerWaitingTimes, consumerThreshold2);
        double r3 = satisfiedCustomers(corporateWaitingTimes, corporateThreshold1);
        double r4 = satisfiedCustomers(corporateWaitingTimes, corporateThreshold2);

        if (print) {
            System.out.println("Consumer handled within 5 minutes : " + r1);
            System.out.println("Consumer handled within 10 minutes : " + r2);
            System.out.println("Corporates handled within 3 minutes : " + r3);
            System.out.println("Corporates handled within 7 minutes : " + r4);
        }
        return new double[]{r1, r2, r3, r4, waitingTimesAverage(consumerList), waitingTimesAverage(corporateList)};
    }


    public static double satisfiedCustomers(double[] waitingTimesArray, double waitingTimeThreshold) {
        int customersBelowThreshold = 0;
        for (double customerWaitingTime : waitingTimesArray) {
            if (customerWaitingTime <= waitingTimeThreshold && customerWaitingTime != -1) customersBelowThreshold++;
        }

        return (double) customersBelowThreshold / (double) waitingTimesArray.length;
    }

    public static void printMatlabArray(String variableName, double[] array) {
        System.out.print(variableName + " = [");
        for (Object item : array) System.out.print(item + ", ");
        System.out.print("];");
        System.out.println();
    }
}