package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {


    public static void main(String[] args) {
        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] sound = new GuitarString[keyboard.length()];
        for (int i = 0; i < keyboard.length(); i++){
            double fre = 440 * Math.pow(2, (i - 24) / 12.0);
            GuitarString string = new GuitarString(fre);
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
