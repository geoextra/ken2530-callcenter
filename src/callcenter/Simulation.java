/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

package simulation;

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[] arrivalTimes = new double[]{0.4, 1.2, 0.5, 1.7, 0.2, 1.6, 0.2, 1.4, 1.9};
        double[] serviceTimes = new double[]{2.0, 0.7, 0.2, 1.1, 3.7, 0.6, 4.0, 4.0, 4.0};


        // Create an eventlist
        CEventList l = new CEventList();
        // A queue for the machine
        Queue q = new Queue();

	/*
	// A source
    Source s = new Source(q,l,"Source 1");

    // A machine
    Machine m = new Machine(q,si,l,"Machine 1");
	*/

        // A sink
        Sink si = new Sink("Sink 1");


        for (int i = 0; i < arrivalTimes.length; i++) {
            l.add(m, 0, arrivalTimes[i]);
        }

        // start the eventlist
        l.start(2000); // 2000 is maximum time
    }
}
