package byow.Input;

import edu.princeton.cs.introcs.StdDraw;

public class KeyBoardInputSource implements InputSource {
    public char getNextKey(){
        char key = StdDraw.nextKeyTyped();
        key = Character.toUpperCase(key);
        return key;
    }
    public boolean possibleNextInput(){
        return StdDraw.hasNextKeyTyped();
    }
}
