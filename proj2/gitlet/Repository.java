package gitlet;

import java.io.File;
import gitlet.Utils.*;

import java.io.IOException;
import java.util.Date;
import java.util.TreeMap;
import java.util.List;


// TODO: any imports you need here

/** Represents a gitlet repository.
 * The structure of a gitlet Repository is as follows:
 *
 * .gitlet/ -- top level folder for all persistent data
 *    - object/ -- folder contains commits and blobs
 *      - commit/ -- file name is commit sha1
 *      - blob/ -- file name is blob sha1
 *    - refs/
 *      - heads/ -- each branch has one file
 *        filename is branch name, commit hashcode as the content
 *      -remote/ -- not finished, for future remote commands
 *    - HEAD -- save current branch name, default as master
 *    - stage -- seems like no need of this.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /** The file store stage class-including stageadd and stagerm */
    public static final File STAGE_File = Utils.join(GITLET_DIR,"stage","stagearea");
    /** current Branch name */
    private static String BRANCH;
    /** Map for stageadd and stageremove
     * stored in stageArea
     */
    private static Stage StageArea = new Stage();

    /**
    * TODO: create the .gitlet directory
     * TODO: has the initial commit under branch master
     * TODO: timestamp will be The Epoch
     * TODO: fail if a gitlet already exist
     */
    public static void setup_Persistance() {
        File BLOB_FOLDER = Utils.join(GITLET_DIR,"OBJECT","BLOB");
        File COMMIT_FOLDER = Utils.join(GITLET_DIR,"OBJECT","COMMIT");
        File STAGE_FOLDER = Utils.join(GITLET_DIR,"stage");
        File HEAD = Utils.join(GITLET_DIR,"HEAD");
        File ref_heads = Utils.join(GITLET_DIR,"ref","heads");
        GITLET_DIR.mkdir();
        COMMIT_FOLDER.mkdirs();
        BLOB_FOLDER.mkdir();
        STAGE_FOLDER.mkdir();
        ref_heads.mkdirs();
        if (!HEAD.exists()) {
            try {
                HEAD.createNewFile(); // error when the file exists.
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        BRANCH = "MASTER";
        Utils.writeContents(HEAD, BRANCH);
        StageArea = new Stage();
        Utils.writeObject(STAGE_File, StageArea);


    }
    public static void updateHEAD(String selfsha1) {
        File HEAD = Utils.join(GITLET_DIR,"HEAD");
        String curr_head = Utils.readContentsAsString(HEAD);
        File ref_heads = Utils.join(GITLET_DIR,"ref","heads",curr_head);
        if (!ref_heads.exists()) {
            try {
                ref_heads.createNewFile(); // error when the file exists.
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        Utils.writeContents(ref_heads,selfsha1);
    }


    public static void init_command() {
        Commit initCommit = new Commit("initial commit",new TreeMap<>(),null);
        initCommit.changetimestamp(new Date(0));
        initCommit.saveCommit();
        updateHEAD(initCommit.getOwnID());
    }

    /**
     *return the sha1 code of the input file
     */
    public static String getsha1(File file) {
        byte [] bytes = Utils.readContents(file);
        // get the sha1 hashcode
        String sha1code = Utils.sha1(bytes);
        return sha1code;
    }
    /*
    save Blob to the blob folder, filename is the hashcode
    file content is content bytes.
     */
    public static void saveBlob(File file) {
        String content = Utils.readContentsAsString(file);

        String blob_hash = getsha1(file);
        File newblob = Utils.join(GITLET_DIR,"OBJECT","BLOB",blob_hash);
        try {
            newblob.createNewFile();// error when the file exists.
            Utils.writeContents(newblob, content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /*
    check if this hashcode in current commit;
     */
    public static boolean toaddincommit(String hashcode) {
        Commit curr_commit = ObtianLastCommit();
        return curr_commit.contain(hashcode);
    }

    public static void add_command(String filename){
        /** create the blob under the object folder
         *if new, add in stagearea mapping and save in blob folder
         *if already exist in the stagearea, check hashcode
         *  - if not same, update.
         *  if identical to the current commit and if in the stagearea,
         *  remove this file from the stagearea.
         */
        File to_add = new File (filename);
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        if (to_add.exists()) {
            String blob_hash = getsha1(to_add);
            //check if in last commit
            if (toaddincommit(blob_hash)) {
                StageArea.stageadd_rm(filename);
            }
            else {
                if (!StageArea.stageadd_contian(blob_hash)) {
                    StageArea.stageadd_put(filename, blob_hash);
                    saveBlob(to_add);
                }
            }
            Utils.writeObject(STAGE_File, StageArea);
        }
        else {
            System.out.println("File does not exist");
        }
    }
    /*
    return head commit at current branch as a commit class
     */
    public static Commit ObtianLastCommit() {
        File HEAD = Utils.join(GITLET_DIR,"HEAD");
        String curr_head = Utils.readContentsAsString(HEAD);
        File ref_heads = Utils.join(GITLET_DIR,"ref","heads",curr_head);
        String LastCommitSha = Utils.readContentsAsString(ref_heads);
        return Commit.fromFile(LastCommitSha);
    }

    public static void commit_command (String msg) {
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        if (StageArea.getStageadd().isEmpty() && StageArea.getStagerm().isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        }
        else if (msg.equals("")) {
            System.out.println("Please enter a commit message");
        }
        Commit curr = ObtianLastCommit();
        TreeMap<String, String> newblobmap = curr.getBlobsha1();
        for (String filename : StageArea.getStageadd().keySet()) {
            newblobmap.put(filename,StageArea.getStageadd().get(filename));
        }
        for (String rmfile : StageArea.getStagerm()) {
            newblobmap.remove(rmfile);
        }
        Commit newcommit = new Commit(msg, newblobmap, curr.getOwnID());
        newcommit.saveCommit();
        updateHEAD(newcommit.getOwnID());
        StageArea.clear();
        Utils.writeObject(STAGE_File, StageArea);

    }
    /**
     * if in stageadd, remove it from the map.
     *
     * if tracked by commit, put it in stagerm and
     delete it in the CWD*/
    public static void rm_command(String filename) {
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        Commit curr_commit = ObtianLastCommit();
        if (StageArea.stageadd_havefile(filename)) {
            String blob_name = StageArea.getStageadd().get(filename);
            StageArea.stageadd_rm(filename);
            System.out.println(blob_name);
            File blob = Utils.join(GITLET_DIR,"OBJECT","BLOB",blob_name);
            blob.delete();
            Utils.writeObject(STAGE_File, StageArea);
        }
        else if (curr_commit.containfile(filename)) {
            StageArea.stagerm_put(filename);
            Utils.writeObject(STAGE_File, StageArea);
            Utils.restrictedDelete(filename);
        }
        else {
            System.out.println("No reason to remove the file");
        }
    }

    public static void logprint(Commit curr) {
        System.out.println("===");
        System.out.println("commit " + curr.getOwnID());
        System.out.println("Date: " + curr.getTime());
        System.out.println(curr.getMessage() + "\n");
    }
    /**Start from the headcommit to initial commit
     * printout the commit information including id,time&message.
     ignore the 2nd parent found in merge commits
     (need to check this part again, don't understand)*/
    public static void log_command() {
        Commit curr_commit = ObtianLastCommit();
        while (curr_commit.getParentID() != null) {
            logprint(curr_commit);
            curr_commit = Commit.fromFile(curr_commit.getParentID());
        }
        logprint(curr_commit);
    }
    /**print logs of all commits in the commit folder
     */
    public static void globallog_command() {
        File COMMIT_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "COMMIT");
        List<String> allcommit = Utils.plainFilenamesIn(COMMIT_FOLDER);
        if (allcommit != null) {
            for (String i : allcommit) {
                Commit curr = Commit.fromFile(i);
                logprint(curr);
            }
        }
    }

    /**find all commits with the given commit msg
     * then print ids of each commit(one per line)
     */
    public static void find_command(String msg) {
        File COMMIT_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "COMMIT");
        List<String> allcommit = Utils.plainFilenamesIn(COMMIT_FOLDER);
        boolean found = false;
        if (allcommit != null) {
            for (String i : allcommit) {
                Commit curr = Commit.fromFile(i);
                if (curr.getMessage().equals(msg)) {
                    System.out.println(curr.getBlobsha1());
                    found = true;
                }
            }
        }
        if (found == false) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**Display branches, files staged for add or rm
     (need to check this part again, don't understand)*/
    public static void status_command() {
        //print branches
        File ref_heads = Utils.join(GITLET_DIR, "ref", "heads");
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        String curr = Utils.readContentsAsString(HEAD);
        System.out.println("=== Branches ===");
        System.out.println("*" + curr);
        List<String> all_heads = Utils.plainFilenamesIn(ref_heads);
        for (String i : all_heads) {
            if (!i.equals(curr)) {
                System.out.println(i);
            }
        }
        System.out.println();
        //print stageadd
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        System.out.println("=== Staged Files ===");
        for (String i : StageArea.getStageadd().keySet()) {
            System.out.println(i);
        }
        System.out.println();
        //print stagerm
        System.out.println("=== Removed Files ===");
        for (String i : StageArea.getStagerm()) {
            System.out.println(i);
        }
        System.out.println();
        //the rest two (extra credit) not finished
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked files ===");
        System.out.println();
    }

    /**Three ways,
     * 1. checkout -- file name
     * --find the file in the head commit
     * --use its hashcode find the blob
     * --Util.write it in CWD(will overwrite if it exists)

     */
    public static void basic_checkout(Commit commit,String filename) {
        if (commit.getBlobsha1() != null) {
            for (String i : commit.getBlobsha1().keySet()) {
                if (i.equals(filename)) {
                    String hashcode = commit.getBlobsha1().get(i);
                    File BLOB = Utils.join(GITLET_DIR,"OBJECT","BLOB",hashcode);
                    String cur_file = Utils.readContentsAsString(BLOB);
                    File file = Utils.join(CWD,filename);
                    Utils.writeContents(file, cur_file);
                    return;
                }

            }
            System.out.println("File does not exist in that commit.");
        }
    }

    public static void checkout_command1(String filename) {
        Commit curr_commit = ObtianLastCommit();
        basic_checkout(curr_commit,filename);
    }
    /**
     * Method 2. checkout commit id --file name
     * --find the commit by its given id
     * --next same as method 1.
            */
    // check if the commit tree has this ID
    public static boolean containID(Commit commit, String ID) {
        if(commit.getOwnID().equals(ID)) {

            return true;
        } else if (commit.getParentID() == null) {
            return false;
        } else {
            Commit next_commit = Commit.fromFile(commit.getParentID());
            return (containID(next_commit, ID));
        }
    }
    //return the commit in the commit tree with the given ID
    public static Commit commitintree(Commit commit, String ID) {
        if(commit.getOwnID().equals(ID)) {
            return commit;
        } else if (commit.getParentID() == null) {
            return null;
        } else {
            Commit next_commit = Commit.fromFile(commit.getParentID());
            return (commitintree(next_commit, ID));
        }
    }
    public static void checkout_command2(String filename,String ID) {
        Commit curr_commit = ObtianLastCommit();
        if (containID(curr_commit,ID)) {
            basic_checkout(commitintree(curr_commit, ID),filename);
        }
        else {
            System.out.println("No commit with that id exists");
        }
    }
    /**
    * Method 3. checkout branch name
     * --find the head commit at the given branch
     * --iterate over all files in the commit
     * --for each file do method 1
            * --change head to current branch
     * --delete files tracked in current branch
     * --but not in the cheked-out branch
     * Failure case ifs for no.3:
     * 1. the given branch name not exist
     * 2. given branch is current branch
     * 3. if a file not tracked in current branch
     * - and tracked in the given branch,do not overwrite but exit.
     */
    // check if this branch exist.
    public static boolean branch_exist (String branchname) {
        File ref_heads = Utils.join(GITLET_DIR,"ref","heads");
        List<String> allbranch = Utils.plainFilenamesIn(ref_heads);
        return (allbranch.contains(branchname));
    }

    //check if a file not tracked in current head commit
    //and tracked in the given branch head

    public static boolean track_branch (String branchname) {
        Commit curr_commit = ObtianLastCommit();
        File branch_head = Utils.join(GITLET_DIR,"ref","heads",branchname);
        String hashcode = Utils.readContentsAsString(branch_head);
        Commit branch_headcommit = Commit.fromFile(hashcode);
        List<String> fileinCWD = Utils.plainFilenamesIn(CWD);
        for (String i : fileinCWD) {
            File file = Utils.join(GITLET_DIR,i);
            String file_shacode = getsha1(file);
            if(!curr_commit.contain(file_shacode)
                    && branch_headcommit.contain(file_shacode)){
                return true;
            }
        }
        return false;
    }
    public static void updatefileinCWD (Commit branch) {
        //delete files
        Commit curr_commit = ObtianLastCommit();
        for (String i : curr_commit.getBlobsha1().keySet()) {
            if (!branch.contain(i)) {
                Utils.restrictedDelete(i);
            }
        }
        //overwrite files
        for (String i : branch.getBlobsha1().keySet()) {
            String hashcode = branch.getBlobsha1().get(i);
            File BLOB = Utils.join(GITLET_DIR, "OBJECT", "BLOB", i);
            byte[] cur_file = Utils.readContents(BLOB);
            Utils.writeContents(GITLET_DIR, cur_file);
        }
    }
    //change the main branch to the given branchname
    public static void change_branch(String branchname) {
        File HEAD = Utils.join(GITLET_DIR,"HEAD");
        BRANCH = branchname;
        Utils.writeContents(HEAD, BRANCH);
    }
    public static void checkout_command3(String branchname) {
        if (branch_exist(branchname)) {
            if (BRANCH != branchname) {
                if (track_branch(branchname)) {
                    File branch_head = Utils.join(GITLET_DIR, "ref", "heads", branchname);
                    String hash_branch = Utils.readContentsAsString(branch_head);
                    Commit branch_headcommit = Commit.fromFile(hash_branch);
                    updatefileinCWD(branch_headcommit);
                    change_branch(branchname);
                } else {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                }
            } else {
                System.out.println("No need to checkout the current branch");
            }
        } else {
            System.out.println("No such branch exists");
        }
    }
    //add a new branch, create a file with the branchname
    //and write the hashcode of the commit in the file
    public static void branch_command(String branchname) {
        if (!branch_exist(branchname)){
            Commit curr_commit = ObtianLastCommit();
            File branch_head = Utils.join(GITLET_DIR,"ref","heads",branchname);
            String hashcode = curr_commit.getOwnID();
            Utils.writeContents(branch_head, hashcode);
        }
        else {
            System.out.println("A branch with that name already exists.");
        }
    }
    // Delete the non-current branch give,
    //only the file in heads folder,not any commits
    public static void rmbranch_command(String branchname) {
        if (!branch_exist(branchname)){
            if (BRANCH.equals(branchname)) {
                File branch_head = Utils.join(GITLET_DIR,"ref","heads",branchname);
                branch_head.delete();
            }
            else {
                System.out.println("Cannot remove the current branch.");
            }
        }
        else {
            System.out.println("A branch with that name does not exist.");
        }
    }
    // essentially checkout of an arbitrary commit
    //also changes the current branch head.
    public static void reset(String ID) {

    }




    public static void setStageArea(Stage stageArea) {
        StageArea = stageArea;
    }
}
