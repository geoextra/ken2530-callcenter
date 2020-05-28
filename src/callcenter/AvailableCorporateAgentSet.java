package callcenter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Set wrapper for available corporate agents
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class AvailableCorporateAgentSet {
    private final static Set<Agent> agentSet = new LinkedHashSet<>();
    private static int k = 2;

    public static void add(Agent agent) {
        agentSet.add(agent);
    }

    public static void remove(Agent agent) {
        agentSet.remove(agent);
    }

    public static int availableShiftAgentsCount(ShiftType shift) {
        int n = 0;
        for (Agent agent : agentSet) {
            if (agent.getShiftType() == shift) n++;
        }

        return n;
    }

    public static boolean enoughIdleShiftAgents(ShiftType shift) {
        return availableShiftAgentsCount(shift) > k;
    }
}
