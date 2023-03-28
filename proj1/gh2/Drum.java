package gh2;
import deque.Deque;
import deque.LinkedListDeque;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;
public class Drum {
    private static final int SR = 44100;      // Sampling Rate
    private static final double DECAY = 1.0; // energy decay factor

    /* Buffer for storing sound data. */

    private Deque<Double> buffer = new LinkedListDeque<>();

    /* Create a guitar string of the given frequency.  */
    public Drum (double frequency) {
        int capacity = (int) Math.round(SR / frequency);


        //buffer.resizeup(capacity);   //only need for arraydeque

        for (int i = 0; i < capacity; i++) {
            buffer.addLast(0.0);
        }
    }


    /* Pluck the guitar string by replacing the buffer with white noise. */
    public void pluck() {


        //       Make sure that your random numbers are different from each
        //       other. This does not mean that you need to check that the numbers
        //       are different from each other. It means you should repeatedly call
        //       Math.random() - 0.5 to generate new random numbers for each array index.
        for (int i = 0; i < buffer.size(); i++){
            double r = Math.random() - 0.5;
            buffer.removeFirst();
            buffer.addLast(r);
        }
    }

    /* Advance the simulation one time step by performing one iteration of
     * the Karplus-Strong algorithm.
     */
    public void tic() {
        double coin = Math.random();

            Double newDouble = Math.pow(-1,Math.round(coin))*(buffer.get(1) + buffer.get(2)) / 2.0 * DECAY;

        buffer.removeFirst();
        buffer.addLast(newDouble);
    }

    /* Return the double at the front of the buffer. */
    public double sample() {

        return buffer.get(1);
    }
    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        Drum[] sound = new Drum[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++){
            double fre = 440 * Math.pow(2, (i - 24) / 12.0);
            Drum string = new Drum(fre);
            sound[i]=string;
        }


        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index >= 0) {
                    sound[index].pluck();
                }
            }
            /* compute the superposition of samples */
            double sample = 0.0;
            for(int i = 0; i<keyboard.length(); i++){
                sample += sound[i].sample();
            }
            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            for(int i = 0; i<keyboard.length(); i++){
                sound[i].tic();
            }
        }
    }
}
