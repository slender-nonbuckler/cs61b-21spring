package gitlet;
import java.io.Serializable;
import java.util.TreeMap;
import java.util.ArrayList;
public class Stage implements Serializable {
    private TreeMap<String, String> stageadd;
    private ArrayList<String> stagerm;

    public Stage() {
        stageadd = new TreeMap<>();
        stagerm = new ArrayList<>();
    }


    public void stageadd_put(String filename, String sha1) {

        stageadd.put(filename, sha1);
    }

    public void stagerm_put(String filename) {

        stagerm.add(filename);
    }

    public void stageadd_rm(String filename) {

        stageadd.remove(filename);
    }

    public boolean stageadd_contian(String hashcode) {
        if (stageadd != null) {
            for (String i : stageadd.values()) {
                if (i.equals(hashcode)) {
                    return true;
                }
                return false;
        }
        }
        return false;
    }

    public boolean stageadd_havefile(String filename) {
        if (stageadd != null) {
            for (String i : stageadd.keySet()) {
                if (i.equals(filename)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }


    public TreeMap<String, String> getStageadd() {

        return stageadd;
    }

    public ArrayList<String> getStagerm() {

        return stagerm;
    }

    public void clear() {
        stageadd = new TreeMap<String, String>();
        stagerm = new ArrayList<String>();
    }
}
