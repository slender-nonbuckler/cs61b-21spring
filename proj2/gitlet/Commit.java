package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date; // TODO: You'll likely use this in this class
import java.text.DateFormat;
import java.util.List;
import java.util.TreeMap;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     * JQ: variables: timestamp, a TreeMap for blobs( file name : blob hashcode)
     * a string for parent hashcode and a string for ownhashcode
     * a mapping( file name : second parent reference) for merging
     * Method: getownID
     * Method: getparentID
     *
     */

    /** The message of this Commit. */
    private String message;

    private Date timestamp;

    private TreeMap<String,String> blobmap;

    private List<String> parentID;

    private String ownID;

    static final File COMMIT_FOLDER = Utils.join(".gitlet","OBJECT","COMMIT");


    /** make the initial commit
     */
    public Commit(String msg,TreeMap<String, String> blob, List<String> parentID) {
        this.message = msg;
        this.timestamp = new Date();
        this.blobmap = blob;
        this.parentID = parentID;
        this.ownID = findOwnID();
    }
    /* TODO: fill in the rest of this class. */
    public List<String> getParentID() {

        return parentID;
    }

    public String getOwnID() {

        return ownID;
    }


    public String getMessage() {
        return this.message;
    }

    public String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(this.timestamp);
    }




    public void saveCommit() {
        String CommitSHA1 = this.getOwnID();
        File CommitFile = Utils.join(".gitlet","OBJECT","COMMIT",CommitSHA1);
        if (!CommitFile.exists()) {
            try {
                CommitFile.createNewFile(); // error when the file exists.
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        Utils.writeObject(CommitFile,this);
    }

    public boolean contain(String hashcode) {
        if (blobmap != null) {
            for (String i : blobmap.values()) {
                if (i.equals(hashcode)) {
                    return true;
                }
                }
            return false;
            }
        return false;
        }

    public boolean containfile(String filename) {
        if (blobmap != null) {
            for (String i : blobmap.keySet()) {
                if (i.equals(filename)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public String findOwnID() {
        byte [] commit = Utils.serialize(this);
        String selfID = Utils.sha1(commit);
        return selfID;
    }

    public void changetimestamp (Date date) {

        this.timestamp = date;
    }


    public TreeMap<String, String> getBlobsha1() {

        return blobmap;
    }



    public static Commit fromFile(String filename) {
        File des_commit = Utils.join(COMMIT_FOLDER,filename);
        Commit c = Utils.<Commit>readObject(des_commit,Commit.class);
        return c;
    }
}


