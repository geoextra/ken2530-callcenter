package callcenter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Set wrapper for available corporate agents
 *
 * @author Joel Karel, Simone Schouten, Nick Bast, Frederick van der Windt and Moritz Gehlhaar
 * @version %I%, %G%
 */
public class AgentSet {
    private final static Set<Agent> agentSet = new LinkedHashSet<>();
    private static int k = 0;

    public static void add(Agent agent) {
        agentSet.add(agent);
    }

    public static int availableShiftCorporateAgentsCount(ShiftType shiftType) {
        int n = 0;
        for (Agent agent : agentSet) {
            if (agent.getShiftType() == shiftType && agent.isCorporate() && !agent.isBusy()) n++;
        }

        return n;
    }

    public static boolean enoughIdleShiftCorporateAgents(ShiftType shiftType) {
        return availableShiftCorporateAgentsCount(shiftType) > k;
    }
}
