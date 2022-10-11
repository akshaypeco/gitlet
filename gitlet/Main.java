package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Akshay Patel
 */
public class Main {

    /** Current working directory. */
    static final File CWD = new File(".");

    /** Main metadata folder. */
    static final File GITLET_FOLDER = Utils.join(CWD, ".gitlet");

    /** Folder for the staging area. */
    static final File STAGING_AREA = Utils.join(GITLET_FOLDER, "stagingArea");

    /** Folder for staging file removal. */
    static final File REMOVAL_AREA = Utils.join(GITLET_FOLDER, "removalArea");

    /** Folder for working trees. */
    static final File WORKING_TREES = Utils.join(GITLET_FOLDER, "workingTrees");


    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        if (!args[0].equals("init")) {
            if (!GITLET_FOLDER.exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                System.exit(0);
            }
        }
        switch (args[0]) {
        case "init":
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            initialize(args);
            break;
        case "add":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            add(args);
            break;
        case "commit":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            commit(args);
            break;
        case "rm":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            rm(args);
            break;
        case "log":
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            log(args);
            break;
        default:
            main2(args);
            break;
        }
        System.exit(0);
    }

    /** Part 2 of main.
     * @param args
     * String of args */
    private static void main2(String[] args) throws IOException {
        switch (args[0]) {
        case "global-log":
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            globalLog(args);
            break;
        case "find":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            find(args);
            break;
        case "status":
            if (args.length != 1) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            status(args);
            break;
        default:
            main3(args);
            break;
        }
        System.exit(0);
    }


    /** Part 3 of main.
     * @param args
     * String of args */
    private static void main3(String[] args) throws IOException {
        switch (args[0]) {
        case "checkout":
            if (args.length <= 1 || args.length >= 5) {
                System.out.println("Incorrect operands");
                System.exit(0);
            }
            if (args.length == 2) {
                if (args[1].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
            }
            if (args.length == 3) {
                if (!args[1].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
            }
            if (args.length == 4) {
                if (!args[2].equals("--")) {
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
            }
            checkout(args);
            break;
        case "branch":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            branch(args);
            break;
        case "rm-branch":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            rmBranch(args);
            break;
        case "reset":
            if (args.length != 2) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            reset(args);
            break;
        case "merge":
            merge(args);
            break;
        default:
            System.out.println("No command with that name exists.");
            break;
        }
        System.exit(0);
    }

    /** Initialize.
     * @param args */
    public static void initialize(String[] args) {
        if (GITLET_FOLDER.exists()) {
            System.out.println("A Gitlet version-control system "
                    + "already exists in the current directory.");
            System.exit(0);
        }
        GITLET_FOLDER.mkdir();
        STAGING_AREA.mkdir();
        REMOVAL_AREA.mkdir();
        WORKING_TREES.mkdir();
        Blob.BLOB_FOLDER.mkdir();
        Commit.COMMIT_FOLDER.mkdir();

        WorkingTree newTree = new WorkingTree("master");
        newTree.changeIsCurrent(true);
        Commit thisCommit = new Commit("initial commit", null);
        thisCommit.saveCommit();
        newTree.addCommit(thisCommit);
        newTree.saveWorkingTree();
    }

    /** Add.
     * @param args */
    public static void add(String[] args) throws IOException {
        if (!Utils.join(CWD, args[1]).exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit thisCommit = thisTree.getHeadCommit();
        Blob testBlob = new Blob(Utils.join(CWD, args[1]));

        if (thisCommit.getBlobMap().containsValue(testBlob.getBlobSHA1())) {
            Files.deleteIfExists(Utils.join(STAGING_AREA, args[1]).toPath());
        } else {
            Files.deleteIfExists(Utils.join(STAGING_AREA, args[1]).toPath());
            Files.copy(Utils.join(CWD, args[1]).toPath(),
                    Utils.join(STAGING_AREA, args[1]).toPath());
        }
        Files.deleteIfExists(Utils.join(REMOVAL_AREA, args[1]).toPath());
    }

    /** Commit.
     * @param args */
    public static void commit(String[] args) {
        if (args[1].isEmpty()) {
            System.out.println("Please enter a commit message.");
            System.exit(0);

        } else if (STAGING_AREA.listFiles().length == 0
                && REMOVAL_AREA.listFiles().length == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);

        }
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit newCommit = new Commit(args[1],
                thisTree.getHeadCommit());
        newCommit.cloneBlob(thisTree.getHeadCommit());
        for (File file : STAGING_AREA.listFiles()) {
            Blob newBlob = new Blob(file);
            newCommit.addBlob(newBlob);
            newBlob.saveBlob();
        }
        for (String file: Utils.plainFilenamesIn(REMOVAL_AREA)) {
            newCommit.removeBlob(file);
        }
        newCommit.saveCommit();
        thisTree.addCommit(newCommit);
        thisTree.saveWorkingTree();
        for (File file : STAGING_AREA.listFiles()) {
            file.delete();
        }
        for (File file : REMOVAL_AREA.listFiles()) {
            file.delete();
        }
    }

    /** Remove file.
     * @param args */
    public static void rm(String[] args) throws IOException {
        Commit thisCommit = WorkingTree.getCurrentBranch().getHeadCommit();
        if ((!thisCommit.getBlobMap().containsKey(args[1]))
            && (!Utils.join(STAGING_AREA, args[1]).exists())) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        Files.deleteIfExists(Utils.join(STAGING_AREA, args[1]).toPath());
        if (thisCommit.getBlobMap().containsKey(args[1])) {
            if (Utils.join(CWD, args[1]).exists()) {
                Files.copy(Utils.join(CWD, args[1]).toPath(),
                        Utils.join(REMOVAL_AREA, args[1]).toPath());
                Utils.restrictedDelete(Utils.join(CWD, args[1]));
            }
        }
    }

    /** Log.
     * @param args */
    public static void log(String[] args) {
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit thisCommit = thisTree.getHeadCommit();
        while (thisCommit != null) {
            System.out.println(thisCommit.getLog());
            thisCommit = thisCommit.getParent();
        }
    }

    /** Global Log.
     * @param args */
    public static void globalLog(String[] args) {
        List<String> allCommits = Utils.plainFilenamesIn(Commit.COMMIT_FOLDER);
        for (String commit : allCommits) {
            System.out.println(Commit.fromFile(commit).getLog());
        }
    }

    /** Find.
     * @param args */
    public static void find(String[] args) {
        int counter = 0;
        for (String file: Utils.plainFilenamesIn(Commit.COMMIT_FOLDER)) {
            if (Commit.fromFile(file).getMessage().equals(args[1])) {
                System.out.println(file);
                counter += 1;
            }
        }
        if (counter == 0) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Branches part of status.
     * @return String */
    private static String statusBranches() {
        String branchesString = "";
        ArrayList<String> branches = new ArrayList<>();
        for (String file: Utils.plainFilenamesIn(WORKING_TREES)) {
            branches.add(file);
        }
        Collections.sort(branches);
        for (String file : branches) {
            if (WorkingTree.fromFile(file).isCurrent()) {
                branches.set(branches.indexOf(file), "*" + file);
            }
        }
        for (String file : branches) {
            branchesString += file + "\n";
        }
        return branchesString;
    }

    /** Staged part of status.
     * @return String */
    private static String statusStaged() {
        String stagedString = "";
        ArrayList<String> staged =
                new ArrayList<>(Utils.plainFilenamesIn(STAGING_AREA));
        Collections.sort(staged);
        for (String file : staged) {
            stagedString += file + "\n";
        }
        return stagedString;
    }

    /** Removed part of status.
     * @param thisCommit
     * the current Commit
     * @return String */
    private static String statusRemoved(Commit thisCommit) {
        String removedString = "";
        ArrayList<String> removed =
                new ArrayList<>(Utils.plainFilenamesIn(REMOVAL_AREA));
        ArrayList<String> keys =
                new ArrayList<String>(thisCommit.getBlobMap().keySet());
        for (int i = 0; i < keys.size(); i += 1) {
            if (!Utils.join(CWD, keys.get(i)).exists()) {
                if (!removed.contains(keys.get(i))) {
                    removed.add(keys.get(i));
                }
            }
        }
        Collections.sort(removed);
        for (String file : removed) {
            removedString += file + "\n";
        }
        return removedString;
    }

    /** Status.
     * @param args */
    public static void status(String[] args) {
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit thisCommit = thisTree.getHeadCommit();

        String branchTitle = "=== Branches ===";

        String stagedTitle = "\n" + "=== Staged Files ===";

        String removedTitle = "\n" + "=== Removed Files ===";


        ArrayList<String> modifications
                = new ArrayList<>(Utils.plainFilenamesIn(CWD));
        String modificationsString = "";
        Collections.sort(modifications);
        HashMap<String, String> modsAndSits =
                new HashMap<String, String>();
        String modificationsTitle =
                "\n" + "=== Modifications Not Staged For Commit ===";
        for (String file : modifications) {
            if ((thisCommit.getBlobMap().containsKey(file))
                && (!thisCommit.getBlobMap().get(file)
                    .equals(Utils.sha1(Utils.join(CWD, file).getName()
                            + Utils.readContentsAsString
                            (Utils.join(CWD, file)))))
                && (!Utils.join(STAGING_AREA, file).exists())) {
                modsAndSits.put(file, " (modified)");
            } else if (!CWD.exists()) {
                String placeHolder = "I dont want to do this";
            } else if ((Utils.join(STAGING_AREA, file).exists())
                        && (!Utils.join(CWD, file).exists())) {
                modsAndSits.put(file, " (deleted)");
            }
        }

        String untrackedTitle = "\n" + "=== Untracked Files ===";
        String untrackedString = "";
        ArrayList<String> untracked =  new ArrayList<>();
        System.out.println(
                branchTitle + "\n" + statusBranches() + stagedTitle + "\n"
                        + statusStaged() +  removedTitle
                        + "\n" + statusRemoved(thisCommit)
                        + modificationsTitle + "\n"
                        + modificationsString + untrackedTitle + "\n"
                        + untrackedString);

    }

    /** Checks if the branch exists.
     * @param args
     * A string of arguments */
    private static void noBranchExists(String[] args) {
        if (!Utils.join(WORKING_TREES, args[1]).exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
    }

    /** Checks if current branch is head.
     * @param branchTree
     * The intended branch */
    private static void currentBranch(WorkingTree branchTree) {
        if (branchTree.isCurrent()) {
            System.out.println("No need to check out the current branch.");
            System.exit(0);
        }
    }

    /** Check if there are untracked files.
     * @param currHead
     * Current head
     * @param branchTree
     * Intended branch */
    private static void untrackedFile(Commit currHead, WorkingTree branchTree) {
        for (String file : Utils.plainFilenamesIn(CWD)) {
            Blob testBlob = new Blob(Utils.join(CWD, file));
            if (!currHead.getBlobMap().containsKey(file)) {
                if ((branchTree.getHeadCommit()
                        .getBlobMap().containsKey(file))
                        && (!branchTree.getHeadCommit().getBlobMap().get(file)
                        .equals(testBlob.getBlobSHA1()))) {
                    System.out.println("There is "
                            + "an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    System.exit(0);
                }
            }
        }
    }

    /** Changes tree as specified.
     * @param thisTree
     * Current tree
     * @param branchTree
     * Intended tree
     * @param currHead
     * Current head
     * @param headOfBranch
     * Head of intended branch */
    private static void changeTree(WorkingTree thisTree,
                                   WorkingTree branchTree,
                                   Commit currHead, Commit headOfBranch) {
        ArrayList<String> currKeys =
                new ArrayList<String>(currHead.getBlobMap().keySet());
        for (String key : currKeys) {
            if ((currHead.getBlobMap().containsKey(key))
                    && (!headOfBranch.getBlobMap().containsKey(key))) {
                Utils.restrictedDelete(key);
            }
        }
        for (File file : STAGING_AREA.listFiles()) {
            file.delete();
        }
        for (File file : REMOVAL_AREA.listFiles()) {
            file.delete();
        }
        thisTree.changeIsCurrent(false);
        thisTree.saveWorkingTree();

        branchTree.changeIsCurrent(true);
        branchTree.saveWorkingTree();
    }

    /** Checkout.
     * @param args */
    public static void checkout(String[] args) throws IOException {
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit currHead = thisTree.getHeadCommit();
        if (args.length == 2) {
            noBranchExists(args);
            WorkingTree branchTree = WorkingTree.fromFile(args[1]);
            currentBranch(branchTree);
            untrackedFile(currHead, branchTree);
            Commit headOfBranch = branchTree.getHeadCommit();
            ArrayList<String> branchKeys =
                    new ArrayList<String>(headOfBranch.getBlobMap().keySet());
            for (String key : branchKeys) {
                Utils.writeContents(Utils.join(CWD, key),
                        Blob.fromFile(headOfBranch.getBlobMap()
                                .get(key)).getContents());
            }
            changeTree(thisTree, branchTree, currHead, headOfBranch);
        } else if (args[1].equals("--")) {
            if (!currHead.getBlobMap().containsKey(args[2])) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }
            Blob thisBlob = Blob.fromFile(currHead.getBlobMap().get(args[2]));
            Utils.writeContents(Utils.join(CWD, args[2]),
                    thisBlob.getContents());

        } else if (args[2].equals("--")) {
            if (args[1].length() == 8) {
                if (!thisTree.getSixDigitCommitMap().containsKey(args[1])) {
                    System.out.println("No commit with that id exists.");
                    System.exit(0);
                }
                Commit commit = Commit.
                        fromFile(thisTree.getSixDigitCommitMap().get(args[1]));
                if (!commit.getBlobMap().containsKey(args[3])) {
                    System.out.println("File does not exist in that commit.");
                    System.exit(0);
                }
                Blob thisBlob = Blob.fromFile(commit.getBlobMap().get(args[3]));
                Utils.writeContents(Utils.join(CWD, args[3]),
                        thisBlob.getContents());

            } else if (args[1].length() != 8) {
                if (!thisTree.getCommitMap().containsKey(args[1])) {
                    System.out.println("No commit with that id exists.");
                    System.exit(0);
                }
                Commit commit = Commit.fromFile(args[1]);
                if (!commit.getBlobMap().containsKey(args[3])) {
                    System.out.println("File does not exist in that commit.");
                    System.exit(0);
                }
                Utils.writeContents(Utils.join(CWD, args[3]),
                        commit.getBlob(args[3]).getContents());
            }
        }
    }

    /** Create branch.
     * @param args */
    public static void branch(String[] args) {
        if (Utils.join(WORKING_TREES, args[1]).exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        WorkingTree newBranch = new WorkingTree(args[1]);
        Commit copiedCommit = WorkingTree.getCurrentBranch().getHeadCommit();
        newBranch.addCommit(copiedCommit);
        newBranch.saveWorkingTree();
    }

    /** Remove branch.
     * @param args */
    public static void rmBranch(String[] args) throws IOException {
        if (!Utils.join(WORKING_TREES, args[1]).exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (WorkingTree.fromFile(args[1]).isCurrent()) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        Files.deleteIfExists(Utils.join(WORKING_TREES, args[1]).toPath());
    }

    /** Reset.
     * @param args */
    public static void reset(String[] args) {
        WorkingTree thisTree = WorkingTree.getCurrentBranch();
        Commit currHead = thisTree.getHeadCommit();
        Commit newHead = new Commit("placeholder", null);
        if (args[1].length() == 6) {
            if (!thisTree.getSixDigitCommitMap().containsKey(args[1])) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            newHead = Commit.fromFile
                    (thisTree.getSixDigitCommitMap().get(args[1]));
            for (String file : Utils.plainFilenamesIn(CWD)) {
                if (!currHead.getBlobMap().containsKey(file)) {
                    if (newHead.getBlobMap().containsKey(file)) {
                        System.out.println("There is "
                                + "an untracked file in the way; "
                                + "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                }
            }
        }
        if (args[1].length() != 6) {
            if (!Utils.join(Commit.COMMIT_FOLDER, args[1]).exists()) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            newHead = Commit.fromFile(args[1]);
            for (String file: Utils.plainFilenamesIn(CWD)) {
                Blob testBlob = new Blob(Utils.join(CWD, file));
                if (!currHead.getBlobMap().containsKey(file)) {
                    if ((newHead.getBlobMap().containsKey(file))) {
                        if (!newHead.getBlobMap()
                                .containsValue(testBlob.getBlobSHA1())) {
                            System.out.println("There is an untracked "
                                    + "file in the way; "
                                    + "delete it, or add and commit it first.");
                            System.exit(0);
                        }
                    }
                }
            }
        }
        reset2(args, newHead, currHead, thisTree);
    }

    /** Reset part 2.
     * @param args
     * args of main
     * @param newHead
     * new head
     * @param currHead
     * current head
     * @param thisTree
     * current branch*/
    public static void reset2(String[] args, Commit newHead,
                              Commit currHead, WorkingTree thisTree) {
        ArrayList<String> keys =
                new ArrayList<String>(newHead.getBlobMap().keySet());
        for (String key : keys) {
            if ((currHead.getBlobMap().containsKey(key))
                    && (!newHead.getBlobMap().containsKey(key))) {
                Utils.restrictedDelete(key);
            } else {
                Utils.writeContents(Utils.join(CWD, key),
                        newHead.getBlob(key).getContents());
            }
        } for (File file : STAGING_AREA.listFiles()) {
            file.delete();
        } for (File file : REMOVAL_AREA.listFiles()) {
            file.delete();
        } if (args[1].length() == 6) {
            thisTree.setHead(thisTree.getSixDigitCommitMap().get(args[1]));
            thisTree.saveWorkingTree();
        } else {
            thisTree.setHead(args[1]);
            thisTree.saveWorkingTree();
        }
    }

    /** Initialize.
     * @param args */
    public static void merge(String[] args) {

    }
}
