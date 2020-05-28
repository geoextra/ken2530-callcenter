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
    private final Set<Agent> agentSet = new LinkedHashSet<>();

    private int k;

    public AgentSet(int k) {
        this.k = k;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void add(Agent agent) {
        agentSet.add(agent);
    }

    public int availableShiftCorporateAgentsCount(ShiftType shiftType) {
        int n = 0;
        for (Agent agent : agentSet) {
            if (agent.getShiftType() == shiftType && agent.isCorporate() && !agent.isBusy()) n++;
        }

        return n;
    }

    public boolean enoughIdleShiftCorporateAgents(ShiftType shiftType) {
        return availableShiftCorporateAgentsCount(shiftType) > k;
    }

    public void reset() {
        agentSet.clear();
    }
}
