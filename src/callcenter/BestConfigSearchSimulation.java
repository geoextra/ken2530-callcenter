package callcenter;

import java.util.LinkedList;

import static callcenter.DateUtils.daysToSeconds;
import static callcenter.DateUtils.minutesToSeconds;

/**
 * Hyper-parameter optimisation
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class BestConfigSearchSimulation {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation.print = false;

        int lowest_cost = Integer.MAX_VALUE;

        int agents_consumer_morning_max = 8;
        int agents_consumer_afternoon_max = 8;
        int agents_consumer_night_max = 8;
        int agents_corporate_morning_max = 8;
        int agents_corporate_afternoon_max = 8;
        int agents_corporate_night_max = 8;
        int k_max = 8;
        for (int agents_consumer_morning = 3; agents_consumer_morning < agents_consumer_morning_max; agents_consumer_morning++) {
            for (int agents_consumer_afternoon = 3; agents_consumer_afternoon < agents_consumer_afternoon_max; agents_consumer_afternoon++) {
                for (int agents_consumer_night = 3; agents_consumer_night < agents_consumer_night_max; agents_consumer_night++) {
                    for (int agents_corporate_morning = 3; agents_corporate_morning < agents_corporate_morning_max; agents_corporate_morning++) {
                        for (int agents_corporate_afternoon = 3; agents_corporate_afternoon < agents_corporate_afternoon_max; agents_corporate_afternoon++) {
                            for (int agents_corporate_night = 3; agents_corporate_night < agents_corporate_night_max; agents_corporate_night++) {
                                for (int k = 0; k < k_max; k++) {
                                    AgentSet agentSet = new AgentSet(k);
                                    CEventList eventList = new CEventList();

                                    Queue consumerQueue = new Queue();
                                    Queue corporateQueue = new Queue();
                                    LinkedList<Customer> consumerList = new LinkedList<>();
                                    LinkedList<Customer> corporateList = new LinkedList<>();

                                    Source consumerSource = new Source(consumerQueue, eventList, "Consumer Source", false, consumerList);
                                    Source corporateSource = new Source(corporateQueue, eventList, "Corporate Source", true, corporateList);

                                    Sink consumerSink = new Sink("Consumer sink");
                                    Sink corporateSink = new Sink("Corporate sink");

                                    int cost = 0;

                                    // 3 shift * 2 agent types * max 20 agents * 10 parameter values
                                    for (int i = 0; i < agents_consumer_morning; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Morning", false, ShiftType.MORNING);
                                        cost += agent.getDailyCost();
                                    }
                                    for (int i = 0; i < agents_consumer_afternoon; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Afternoon", false, ShiftType.AFTERNOON);
                                        cost += agent.getDailyCost();
                                    }
                                    for (int i = 0; i < agents_consumer_night; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Night", false, ShiftType.NIGHT);
                                        cost += agent.getDailyCost();
                                    }
                                    for (int i = 0; i < agents_corporate_morning; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Corporate Agent Morning", true, ShiftType.MORNING);
                                        cost += agent.getDailyCost();
                                    }
                                    for (int i = 0; i < agents_corporate_afternoon; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Corporate Agent Afternoon", true, ShiftType.AFTERNOON);
                                        cost += agent.getDailyCost();
                                    }
                                    for (int i = 0; i < agents_corporate_night; i++) {
                                        Agent agent = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Corporate Agent Night", true, ShiftType.NIGHT);
                                        cost += agent.getDailyCost();
                                    }

                                    double daysToSimulate = 1;
                                    double simulationTime = daysToSeconds(daysToSimulate);
                                    assert simulationTime == 86400;
                                    eventList.start(simulationTime);

                                    // double[] waitingTimesConsumer = waitingTimes(consumerList);
                                    // double[] waitingTimesCorporate = waitingTimes(corporateList);

                                    if (cost < lowest_cost && performanceCheck(consumerList, corporateList)) {
                                        lowest_cost = cost;
                                        System.out.println("Cost: " + cost);
                                        System.out.println("Config: " + "\nagents_consumer_morning=" + agents_consumer_morning +
                                                "\nagents_consumer_morning=" + agents_consumer_morning +
                                                "\nagents_consumer_afternoon=" + agents_consumer_afternoon +
                                                "\nagents_consumer_night=" + agents_consumer_night +
                                                "\nagents_corporate_morning=" + agents_corporate_morning +
                                                "\nagents_corporate_afternoon=" + agents_corporate_afternoon +
                                                "\nagents_corporate_night=" + agents_corporate_night +
                                                "\nk=" + k

                                        );
                                    }
                                    ;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static double[] waitingTimes(LinkedList<Customer> customerList) {
        double[] waitingTimes = new double[customerList.size()];

        if (Simulation.print) System.out.print("[");
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

            if (Simulation.print) if (Simulation.print) System.out.print(waitingTime + ", ");
        }
        if (Simulation.print) System.out.println("]");
        return waitingTimes;
    }


    public static boolean performanceCheck(LinkedList<Customer> consumerList, LinkedList<Customer> corporateList) {
        double[] consumerWaitingTimes = waitingTimes(consumerList);
        double[] corporateWaitingTimes = waitingTimes(corporateList);

        double consumerThreshold1 = minutesToSeconds(5);
        double consumerThreshold2 = minutesToSeconds(10);

        double corporateThreshold1 = minutesToSeconds(3);
        double corporateThreshold2 = minutesToSeconds(7);

        if (Simulation.print)
            System.out.println("Consumer handled within 5 minutes : " + satisfiedCustomers(consumerWaitingTimes, consumerThreshold1));
        if (Simulation.print)
            System.out.println("Consumer handled within 10 minutes : " + satisfiedCustomers(consumerWaitingTimes, consumerThreshold2));
        if (Simulation.print)
            System.out.println("Corporates handled within 3 minutes : " + satisfiedCustomers(corporateWaitingTimes, corporateThreshold1));
        if (Simulation.print)
            System.out.println("Corporates handled within 7 minutes : " + satisfiedCustomers(corporateWaitingTimes, corporateThreshold2));
        boolean customers1 = satisfiedCustomers(consumerWaitingTimes, consumerThreshold1) >= 0.9;
        boolean customers2 = satisfiedCustomers(consumerWaitingTimes, consumerThreshold2) >= 0.95;
        boolean corporates1 = satisfiedCustomers(corporateWaitingTimes, corporateThreshold1) >= 0.95;
        boolean corporates2 = satisfiedCustomers(corporateWaitingTimes, corporateThreshold2) >= 0.99;
        return customers1 && customers2 && corporates1 && corporates2;
    }

    public static double satisfiedCustomers(double[] waitingTimesArray, double waitingTimeThreshold) {
        int customersBelowThreshold = 0;
        for (double customerWaitingTime : waitingTimesArray) {
            if (customerWaitingTime <= waitingTimeThreshold && customerWaitingTime != -1) customersBelowThreshold++;
        }

        return (double) customersBelowThreshold / (double) waitingTimesArray.length;
    }
}
