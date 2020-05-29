package callcenter;

import java.util.LinkedList;
import java.util.Random;

import static callcenter.DateUtils.daysToSeconds;

/**
 * Simulation main class with best config
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class Simulation {
    public static final Random randomGenerator = new Random(42);
    public static boolean print = true;

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

        final int k = 2;
        AgentSet agentSet = new AgentSet(k);

        for (int i = 0; i < 5; i++) {
            Agent consumerAgent1 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Morning", false, ShiftType.MORNING);
            Agent consumerAgent2 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Afternoon", false, ShiftType.AFTERNOON);
            Agent consumerAgent3 = new Agent(agentSet, consumerQueue, corporateQueue, consumerSink, eventList, "Consumer Agent Night", false, ShiftType.NIGHT);

            Agent corporateAgent1 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent Morning", true, ShiftType.MORNING);
            Agent corporateAgent2 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent Afternoon", true, ShiftType.AFTERNOON);
            Agent corporateAgent3 = new Agent(agentSet, consumerQueue, corporateQueue, corporateSink, eventList, "Corporate Agent Night", true, ShiftType.NIGHT);
        }
        double daysToSimulate = 1;
        double simulationTime = daysToSeconds(daysToSimulate);
        assert simulationTime == 86400;
        eventList.start(simulationTime);
    }
}