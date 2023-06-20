package gitlet;

import java.io.File;
import gitlet.Utils.*;

import java.io.IOException;
import java.util.*;


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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");
    /**
     * The file store stage class-including stageadd and stagerm
     */
    public static final File STAGE_File = Utils.join(GITLET_DIR, "stage", "stagearea");
    /**
     * current Branch name
     */
    //private static String BRANCH;
    /**
     * Map for stageadd and stageremove
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
        File BLOB_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "BLOB");
        File COMMIT_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "COMMIT");
        File STAGE_FOLDER = Utils.join(GITLET_DIR, "stage");
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        File ref_heads = Utils.join(GITLET_DIR, "ref", "heads");
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
        Utils.writeContents(HEAD, "master");
        StageArea = new Stage();
        Utils.writeObject(STAGE_File, StageArea);


    }

    /**
     * Assign the current branch name
     * to the parameter BRANCH
     */
    public static String get_BRANCH() {
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        return (Utils.readContentsAsString(HEAD));
    }
    public static void updateHEAD(String selfsha1) {
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        String curr_head = Utils.readContentsAsString(HEAD);
        File ref_heads = Utils.join(GITLET_DIR, "ref", "heads", curr_head);
        if (!ref_heads.exists()) {
            try {
                ref_heads.createNewFile(); // error when the file exists.
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        Utils.writeContents(ref_heads, selfsha1);
    }


    public static void init_command() {
        Commit initCommit = new Commit("initial commit", new TreeMap<>(), null);
        initCommit.changetimestamp(new Date(0));
        initCommit.saveCommit();
        updateHEAD(initCommit.getOwnID());
    }

    /**
     * return the sha1 code of the input file
     */
    public static String getsha1(File file) {
        byte[] bytes = Utils.readContents(file);
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
        File newblob = Utils.join(GITLET_DIR, "OBJECT", "BLOB", blob_hash);
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

    public static boolean fileincommit(String filename) {
        Commit curr_commit = ObtianLastCommit();
        return curr_commit.getBlobsha1().containsKey(filename);
    }

    public static void add_command(String filename) {
        /** create the blob under the object folder
         *if new, add in stagearea mapping and save in blob folder
         *if already exist in the stagearea, check hashcode
         *  - if not same, update.
         *  if identical to the current commit and if in the stagearea,
         *  remove this file from the stagearea.
         */
        File to_add = Utils.join(CWD,filename);
        Commit curr_commit = ObtianLastCommit();
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        if (to_add.exists()) {
            String blob_hash = getsha1(to_add);
            //check if in last commit
            if (toaddincommit(blob_hash)) {
                if (StageArea.stageadd_havefile(filename)) {
                    StageArea.stageadd_rm(filename);
                }
                else if (StageArea.getStagerm().contains(filename)) {
                    StageArea.getStagerm().remove(filename);
                }
                else if (!fileincommit(filename)) {
                    StageArea.stageadd_put(filename, blob_hash);
                }
                Utils.writeObject(STAGE_File, StageArea);
            }
            else {
                if (!StageArea.stageadd_contian(blob_hash)) {
                    StageArea.stageadd_put(filename, blob_hash);
                    saveBlob(to_add);
                }
            }
            Utils.writeObject(STAGE_File, StageArea);
        } else {
            System.out.println("File does not exist");
        }
    }

    /*
    return head commit at current branch as a commit class
     */
    public static Commit ObtianLastCommit() {
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        String curr_head = Utils.readContentsAsString(HEAD);
        File ref_heads = Utils.join(GITLET_DIR, "ref", "heads", curr_head);
        String LastCommitSha = Utils.readContentsAsString(ref_heads);
        return Commit.fromFile(LastCommitSha);
    }

    public static void commit_command(String msg, String second_parentID) {
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        if (StageArea.getStageadd().isEmpty() && StageArea.getStagerm().isEmpty()) {
            System.out.println("No changes added to the commit");
            return;
        } else if (msg.equals("")) {
            System.out.println("Please enter a commit message");
        }
        Commit curr = ObtianLastCommit();
        TreeMap<String, String> newblobmap = curr.getBlobsha1();
        for (String filename : StageArea.getStageadd().keySet()) {
            newblobmap.put(filename, StageArea.getStageadd().get(filename));
        }
        for (String rmfile : StageArea.getStagerm()) {
            newblobmap.remove(rmfile);
        }
        List<String> parents = new ArrayList<>();
        parents.add(curr.getOwnID());
        if (second_parentID != null) {
            parents.add(second_parentID);
        }
        Commit newcommit = new Commit(msg, newblobmap, parents);
        newcommit.saveCommit();
        updateHEAD(newcommit.getOwnID());
        StageArea.clear();
        Utils.writeObject(STAGE_File, StageArea);

    }

    /**
     * if in stageadd, remove it from the map.
     * <p>
     * if tracked by commit, put it in stagerm and
     * delete it in the CWD
     */
    public static void rm_command(String filename) {
        StageArea = Utils.readObject(STAGE_File, Stage.class);
        Commit curr_commit = ObtianLastCommit();
        if (StageArea.stageadd_havefile(filename)) {
            String blob_name = StageArea.getStageadd().get(filename);
            StageArea.stageadd_rm(filename);
            //File blob = Utils.join(GITLET_DIR, "OBJECT", "BLOB", blob_name);
            //blob.delete();
            Utils.writeObject(STAGE_File, StageArea);
        } else if (curr_commit.containfile(filename)) {
            StageArea.stagerm_put(filename);
            Utils.writeObject(STAGE_File, StageArea);
            Utils.restrictedDelete(filename);
        } else {
            System.out.println("No reason to remove the file");
        }
    }

    public static void logprint(Commit curr) {
        System.out.println("===");
        System.out.println("commit " + curr.getOwnID());
        System.out.println("Date: " + curr.getTime());
        System.out.println(curr.getMessage() + "\n");
    }

    public static void logprintMerge(Commit curr) {
        System.out.println("===");
        System.out.println("commit " + curr.getOwnID());
        String parent1 = curr.getParentID().get(0);
        String parent2 = curr.getParentID().get(1);
        System.out.println("Merge: " + parent1.substring(0, 7) + " "
                + parent2.substring(0, 7));
        System.out.println("Date: " + curr.getTime());
        System.out.println(curr.getMessage() + "\n");
    }

    /**
     * Start from the headcommit to initial commit
     * printout the commit information including id,time&message.
     * ignore the 2nd parent found in merge commits
     * (need to check this part again, don't understand)
     */
    public static void log_command() {
        Commit curr_commit = ObtianLastCommit();
        while (curr_commit.getParentID() != null) {
            if (curr_commit.getParentID().size() == 1) {
                logprint(curr_commit);
            }
            else {
                logprintMerge(curr_commit);
            }
            curr_commit = Commit.fromFile(curr_commit.getParentID().get(0));
        }
        logprint(curr_commit);
    }

    /**
     * print logs of all commits in the commit folder
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

    /**
     * find all commits with the given commit msg
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
                    System.out.println(curr.getOwnID());
                    found = true;
                }
            }
        }
        if (found == false) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * Display branches, files staged for add or rm
     * (need to check this part again, don't understand)
     */
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
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /**
     * Three ways,
     * 1. checkout -- file name
     * --find the file in the head commit
     * --use its hashcode find the blob
     * --Util.write it in CWD(will overwrite if it exists)
     */
    public static void basic_checkout(Commit commit, String filename) {
        if (commit.getBlobsha1() != null) {
            for (String i : commit.getBlobsha1().keySet()) {
                if (i.equals(filename)) {
                    String hashcode = commit.getBlobsha1().get(i);
                    File BLOB = Utils.join(GITLET_DIR, "OBJECT", "BLOB", hashcode);
                    String cur_file = Utils.readContentsAsString(BLOB);
                    File file = Utils.join(CWD, filename);
                    Utils.writeContents(file, cur_file);
                    return;
                }

            }
            System.out.println("File does not exist in that commit.");
        }
    }

    public static void checkout_command1(String filename) {
        Commit curr_commit = ObtianLastCommit();
        basic_checkout(curr_commit, filename);
    }

    /**
     * Method 2. checkout commit id --file name
     * --find the commit by its given id
     * --next same as method 1.
     */
    // check if the commit tree has this ID
    public static boolean containID(String ID) {
        File COMMIT_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "COMMIT");
        List commit = Utils.plainFilenamesIn(COMMIT_FOLDER);
        return (commit.contains(ID));
    }

    //return the commit in the commit tree with the given ID
    public static Commit commitintree(String ID) {
        File this_COMMIT = Utils.join(GITLET_DIR, "OBJECT", "COMMIT", ID);
        Commit commit = Utils.readObject(this_COMMIT, Commit.class);
        return commit;
    }

    public static void checkout_command2(String filename, String ID) {
        if (containID(ID)) {
            basic_checkout(commitintree(ID), filename);
        }
        else if (ID.length() < 40) {
            File COMMIT_FOLDER = Utils.join(GITLET_DIR, "OBJECT", "COMMIT");
            List<String> commit = Utils.plainFilenamesIn(COMMIT_FOLDER);
            int occurence = 0;
            String commitID = "";
            for (String i : commit) {
                if (ID.equals(i.substring(0, ID.length()))) {
                    occurence += 1;
                    commitID = i;
                }
            }
            if (occurence == 1) {
                basic_checkout(commitintree(commitID), filename);
            }
        }
        else {
            System.out.println("No commit with that id exists.");
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
    public static boolean branch_exist(String branchname) {
        File ref_heads = Utils.join(GITLET_DIR, "ref", "heads");
        List<String> allbranch = Utils.plainFilenamesIn(ref_heads);
        return (allbranch.contains(branchname));
    }

    //return the headcommit of the given branch
    public static Commit headcommit(String branchname) {
        File branch_head = Utils.join(GITLET_DIR, "ref", "heads", branchname);
        String hashcode = Utils.readContentsAsString(branch_head);
        Commit branch_headcommit = Commit.fromFile(hashcode);
        return branch_headcommit;
    }

    //check if a file not tracked in current head commit
    //and tracked in the given branch head


    public static boolean track_branch(Commit commit) {
        Commit curr_commit = ObtianLastCommit();
        List<String> fileinCWD = Utils.plainFilenamesIn(CWD);
        for (String i : fileinCWD) {
            File file = Utils.join(CWD, i);
            String file_shacode = getsha1(file);
            if (!curr_commit.contain(file_shacode)
                    && commit.contain(file_shacode)) {
                return true;
            }
            else if (!curr_commit.containfile(i)
                    && commit.containfile(i)) {
                return true;
            }
        }
        return false;
    }

    public static void updatefileinCWD(Commit branch) {
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
            File BLOB = Utils.join(GITLET_DIR, "OBJECT", "BLOB", hashcode);
            byte[] cur_file = Utils.readContents(BLOB);
            File file = Utils.join(CWD, i);
            Utils.writeContents(file, cur_file);
        }
    }

    //change the main branch to the given branchname
    public static void change_branch(String branchname) {
        File HEAD = Utils.join(GITLET_DIR, "HEAD");
        Utils.writeContents(HEAD, branchname);
    }

    public static void checkout_command3(String branchname) {
        if (branch_exist(branchname)) {
            if (!get_BRANCH().equals(branchname)) {
                Commit branch_headcommit = headcommit(branchname);
                if (!track_branch(branch_headcommit)) {
                    updatefileinCWD(branch_headcommit);
                    change_branch(branchname);
                    StageArea.clear();
                    Utils.writeObject(STAGE_File, StageArea);
                } else {
                    System.out.println("There is an untracked file in the way;"
                            + " delete it, or add and commit it first.");
                }
            } else {
                System.out.println("No need to checkout the current branch.");
            }
        } else {
            System.out.println("No such branch exists.");
        }
    }

    //add a new branch, create a file with the branchname
    //and write the hashcode of the commit in the file
    public static void branch_command(String branchname) {
        if (!branch_exist(branchname)) {
            Commit curr_commit = ObtianLastCommit();
            File branch_head = Utils.join(GITLET_DIR, "ref", "heads", branchname);
            String hashcode = curr_commit.getOwnID();
            Utils.writeContents(branch_head, hashcode);
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    // Delete the non-current branch given,
    //only the file in heads folder,not any commits
    public static void rmbranch_command(String branchname) {
        if (branch_exist(branchname)) {
            if (!get_BRANCH().equals(branchname)) {
                File branch_head = Utils.join(GITLET_DIR, "ref", "heads", branchname);
                branch_head.delete();
            } else {
                System.out.println("Cannot remove the current branch.");
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    // essentially checkout of an arbitrary commit
    //also changes the current branch head.
    public static void reset_command(String ID) {
        if (containID(ID)) {
            Commit branch_headcommit = commitintree(ID);
            if (!track_branch(branch_headcommit)) {
                updatefileinCWD(branch_headcommit);
                updateHEAD(branch_headcommit.getOwnID());
                StageArea.clear();
                Utils.writeObject(STAGE_File, StageArea);
            } else {
                System.out.println("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        } else {
            System.out.println("No commit with that id exists");
        }
    }

    /**
     * 1. first need to find the split point by BFS
     *
     * @param branchname
     */
    public static void merge_command(String branchname) {
        check_branchexisting(branchname);
        check_stagearea();
        check_mergeself(branchname);
        check_untrakced(branchname);
        Commit curr_commit = ObtianLastCommit();
        Commit branch = headcommit(branchname);
        Commit splitpoint = findSplitPoint(curr_commit, branch);
        //System.out.println(splitpoint.getOwnID());
        //System.out.println(branch.getOwnID());
        //System.out.println(curr_commit.getOwnID());
        ifsplitpointisgivenbranch(splitpoint, branch);
        ifsplitpointiscurrbranch(splitpoint, curr_commit, branchname);

        for (String i : getAllFiles(splitpoint, curr_commit, branch)) {
            int mergecase = findMergeCase(i, splitpoint, curr_commit, branch);
            System.out.println(i);
            System.out.println(mergecase);
            switch (findMergeCase(i, splitpoint, curr_commit, branch)) {
                case 0:
                    break;
                    //modified in other but not head
                case 1:
                    overwritefile(branch, i);
                    add_command(i);
                    break;
                    // only exist in branch
                case 5:
                    overwritefile(branch, i);
                    add_command(i);
                    break;
                    // only exist in splitpoint and curr,but not modified.
                case 6:
                    rm_command(i);
                    break;
                case 8:
                    //replace contents in the file with both contents
                    //in curr and branch commit and stage the result.
                    String currContents = "";
                    if (curr_commit.containfile(i)) {
                        String hashcode_curr = curr_commit.getBlobsha1().get(i);
                        File curr_blobfile = Utils.join(GITLET_DIR, "OBJECT", "BLOB", hashcode_curr);
                        currContents = Utils.readContentsAsString(curr_blobfile);
                    }
                    String branchContents = "";
                    if (branch.containfile(i)) {
                        String hashcode_br = branch.getBlobsha1().get(i);
                        File branch_blobfile = Utils.join(GITLET_DIR, "OBJECT", "BLOB", hashcode_br);
                        branchContents = Utils.readContentsAsString(branch_blobfile);
                    }
                    String combine = "<<<<<<< HEAD\n" + currContents + "=======\n"
                            + branchContents + ">>>>>>>\n";
                    File combine_file = Utils.join(CWD, i);
                    Utils.writeContents(combine_file, combine);
                    add_command(i);
                    System.out.println("Encountered a merge conflict.");
                    break;
            }
        }
        String commit_msg = "Merged " + branchname + " into " + get_BRANCH() + ".";
        commit_command(commit_msg, branch.getOwnID());
    }


    /**
     * use the BFS method to get the path from the given
     * two commits to the initial commit, if it occurs that
     * the visited set contains the current commit, return
     * the commit which is the split point.
     *
     * @param curr_commit
     * @param branch_commit
     * @return
     */

    public static Commit findSplitPoint(Commit curr_commit, Commit branch_commit) {
        Queue<String> path = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        path.add(curr_commit.getOwnID());
        path.add(branch_commit.getOwnID());
        while (!path.isEmpty()) {
            String ID = path.poll();
            Commit commit = Commit.fromFile(ID);
            if (visited.contains(ID)) {
                return commit;
            }
            visited.add(ID);
            if (commit.getParentID() != null) {
                for (String parentID : commit.getParentID()) {
                    path.add(parentID);
                }
            }
        }
        return null;
    }

    /**
     * Determine which cases is for merge
     * refer to design doc for each part
     * case1&5 need to checkout and stage
     * case 6 is for removal and untrack
     * case 8 is the confilct.
     * rest caes has no changes.
     *
     * @param filename
     * @param splitpoint
     * @param currhead
     * @param branch
     * @return
     */
    public static int findMergeCase(String filename, Commit splitpoint,
                                    Commit currhead, Commit branch) {
        Map<String, String> splitpointMap = splitpoint.getBlobsha1();
        Map<String, String> currMap = currhead.getBlobsha1();
        Map<String, String> branchMap = branch.getBlobsha1();
        int commonNo = 0;
        int caseNo = 0;
        if (currhead.containfile(filename)) {
            commonNo += 1;
        }
        if (branch.containfile(filename)) {
            commonNo += 2;
        }
        if (splitpoint.containfile(filename)) {
            commonNo += 4;
        }
        if (commonNo == 7) {
            System.out.println(splitpointMap.get(filename));
            System.out.println(currMap.get(filename));
            System.out.println(branchMap.get(filename));
            if (splitpointMap.get(filename).equals(branchMap.get(filename)) &&
                    (!splitpointMap.get(filename).equals(currMap.get(filename)))) {
                caseNo = 1;
                return caseNo;
            } else if ((!splitpointMap.get(filename).equals(branchMap.get(filename)) &&
                    (!splitpointMap.get(filename).equals(currMap.get(filename))) &&
                    (!branchMap.get(filename).equals(currMap.get(filename))))) {
                caseNo = 8;
                return caseNo;
            }
        }
        if (commonNo == 2) {
            caseNo = 5;
            return caseNo;
        }
        if (commonNo == 3) {
            if (!branchMap.get(filename).equals(currMap.get(filename))) {
                caseNo = 8;
                return caseNo;
            }
        }
        if (commonNo == 5) {
            if (splitpointMap.get(filename).equals(currMap.get(filename))) {
                caseNo = 6;
                return caseNo;
            } else {
                caseNo = 8;
                return caseNo;
            }
        }
        if (commonNo == 6) {
            if (!splitpointMap.get(filename).equals(branchMap.get(filename))) {
                caseNo = 8;
                return caseNo;
            }
        }
        return caseNo;
    }

    public static void check_branchexisting(String branchname) {
        if (!branch_exist(branchname)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    public static void check_stagearea() {
        if (!StageArea.getStageadd().isEmpty() && !StageArea.getStagerm().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
    }

    public static void check_mergeself(String branchname) {
        if (get_BRANCH().equals(branchname)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
    }

    // check if an untracked file in the current commit
    // would be overwritten or delete, i.e. tracked in
    //the branch head.
    public static void check_untrakced(String branchname) {
        Commit branch_headcommit = headcommit(branchname);
        if (track_branch(branch_headcommit)) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it, or add and commit it first.");
            System.exit(0);
        }
    }


    public static void ifsplitpointisgivenbranch(Commit splitpoint, Commit branchhead) {
        if (splitpoint.getOwnID().equals(branchhead.getOwnID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
    }

    public static void ifsplitpointiscurrbranch(Commit splitpoint, Commit currhead, String branch) {
        if (splitpoint.getOwnID().equals(currhead.getOwnID())) {
            System.out.println("Current branch fast-forwarded.");
            checkout_command3(branch);
            System.exit(0);
        }
    }

    /**
     * return a set of all filenames
     * contain in the commit of splitpoint, current and branch
     */
    public static Set<String> getAllFiles(Commit splitpoint, Commit curr, Commit branch) {
        Set<String> set = new HashSet<>();
        set.addAll(splitpoint.getBlobsha1().keySet());
        set.addAll(curr.getBlobsha1().keySet());
        set.addAll(branch.getBlobsha1().keySet());
        return set;
    }

    /**
     * overwrite the file in CWD with the version of the
     * given filename in the given commit
     */
    public static void overwritefile(Commit commit, String filename) {
        if (commit.getBlobsha1().containsKey(filename)) {
            String hashcode = commit.getBlobsha1().get(filename);
            File BLOB = Utils.join(GITLET_DIR, "OBJECT", "BLOB", hashcode);
            File file = Utils.join(CWD, filename);
            byte[] cur_file = Utils.readContents(BLOB);
            Utils.writeContents(file, cur_file);
        }
    }
}
