package gitlet;
import java.io.File;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs("init", args, 1);
                File git = new File(".gitlet");
                if (git.exists()) {
                    System.out.println("A gitlet version-control system"
                            + " already exists in the current directory.");
                }
                else {
                    Repository.setup_Persistance();
                    Repository.init_command();
                }

                break;
            case "add":
                validateNumArgs("add", args, 2);
                validate_initialized();
                String toadd = args[1];
                Repository.add_command(toadd);
                break;
            case "commit":
                validateNumArgs("commit", args, 2);
                validate_initialized();
                String msg = args[1];
                Repository.commit_command(msg,null);
                break;
            case "rm":
                validateNumArgs("rm", args, 2);
                validate_initialized();
                String filename = args[1];
                Repository.rm_command(filename);
                break;
            case "log":
                validateNumArgs("log", args, 1);
                validate_initialized();
                Repository.log_command();
                break;
            case "global-log":
                validateNumArgs("global-log", args, 1);
                validate_initialized();
                Repository.globallog_command();
                break;
            case "find":
                validateNumArgs("find", args, 2);
                validate_initialized();
                String msg_find = args[1];
                Repository.find_command(msg_find);
                break;
            case "status":
                validateNumArgs("status", args, 1);
                validate_initialized();
                Repository.status_command();
                break;
            case "checkout":
                validate_checkout(args);
                validate_initialized();
                break;
            case "branch":
                validateNumArgs("branch", args, 2);
                validate_initialized();
                String branchname = args[1];
                Repository.branch_command(branchname);
                break;
            case "rm-branch":
                validateNumArgs("rm-branch", args, 2);
                validate_initialized();
                String branch = args[1];
                Repository.rmbranch_command(branch);
                break;
            case "reset":
                validateNumArgs("reset", args, 2);
                validate_initialized();
                String commit_ID = args[1];
                Repository.reset_command(commit_ID);
                break;
            case "merge":
                validateNumArgs("merge", args, 2);
                validate_initialized();
                String mergebranch = args[1];
                Repository.merge_command(mergebranch);
                break;

            default:
                validate_commandexist();
        }
    }
    /**
     * Checks the number of arguments versus the expected number,
     * throws a RuntimeException if they do not match.
     *
     * @param cmd Name of command you are validating
     * @param args Argument array from command line
     * @param n Number of expected arguments
     */
    public static void validateNumArgs(String cmd, String[] args, int n) {
        if (args.length != n) {
            throw new RuntimeException(
                    "Incorrect operands.");
        }
    }
    public static void validate_checkout(String[] args) {
        if (args.length == 2) {
            String branchname = args[1];
            Repository.checkout_command3(branchname);
        } else if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            String filename = args[2];
            Repository.checkout_command1(filename);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            String id = args[1];
            String filename = args[3];
            Repository.checkout_command2(filename, id);
        } else {
            throw new RuntimeException(
                    "Incorrect operands.");
        }
    }

    public static void validate_commandexist() {
        System.out.println("No command with that name exists.");
        System.exit(0);
    }
    public static void validate_initialized() {
        if (!Repository.GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}

