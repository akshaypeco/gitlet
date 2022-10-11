package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

/** WorkingTree class that keeps track of branches and commits.
 *  @author Akshay Patel*/
public class WorkingTree implements Serializable {

    /** Name of WorkingTree. */
    private String _name;

    /** Head of commit tree, in terms of commit string. */
    private String _head;

    /** TreeMap of Commits and their respective log messages. */
    private TreeMap<String, String> _commitsAndMessages;

    /** TreeMap of Commits in their 6-digit SHA-1
     * codes and their respective full SHA-1 codes. */
    private TreeMap<String, String> _commitSixDigitAndFull;

    /** ArrayList of Commits. */
    private ArrayList<String> _commits;

    /** Is this the current branch. */
    private boolean _isCurrent;

    /** Initializes WorkingTree class.
     * @param name */
    public WorkingTree(String name) {
        _name = name;
        _isCurrent = false;
        _commitsAndMessages = new TreeMap<String, String>();
        _commitSixDigitAndFull = new TreeMap<String, String>();
        _commits = new ArrayList<String>();
    }

    /** Saves WorkingTree object into file. */
    public void saveWorkingTree() {
        Utils.writeObject(Utils.join(Main.WORKING_TREES, _name), this);
    }

    /** Returns whether branch is current.
     * @return boolean */
    public boolean isCurrent() {
        return _isCurrent;
    }

    /** Change whether branch is current.
     * @param bool */
    public void changeIsCurrent(boolean bool) {
        _isCurrent = bool;
    }

    /** Gets WorkingTree object from file.
     * @param name
     * Name of WorkingTree file
     * @return WorkingTree */
    public static WorkingTree fromFile(String name) {
        return Utils.readObject(Utils.join(Main.WORKING_TREES, name),
                WorkingTree.class);
    }

    /** Get current branch.
     * @return WorkingTree */
    public static WorkingTree getCurrentBranch() {
        WorkingTree foundTree = new WorkingTree("placeholder");
        for (String file: Utils.plainFilenamesIn(Main.WORKING_TREES)) {
            if (WorkingTree.fromFile(file).isCurrent()) {
                foundTree = WorkingTree.fromFile(file);
                break;
            }
        }
        return foundTree;
    }

    /** Gets commit map of branch.
     * @return TreeMap */
    public TreeMap<String, String> getCommitMap() {
        return _commitsAndMessages;
    }

    /** Gets 6-digit commit map of branch.
     * @return TreeMap */
    public TreeMap<String, String> getSixDigitCommitMap() {
        return _commitSixDigitAndFull;
    }

    /** Add commit to branch.
     * @param commit */
    public void addCommit(Commit commit) {
        _commits.add(commit.getCodeName());
        _commitsAndMessages.put(commit.getCodeName(), commit.getLog());
        _commitSixDigitAndFull.put(commit.getSixSHA1(), commit.getCodeName());
        _head = commit.getCodeName();
    }

    /** Sets head of branch.
     * @param sha1 */
    public void setHead(String sha1) {
        _head = sha1;
    }

    /** Get head commit.
     * @return Commit */
    public Commit getHeadCommit() {
        return Commit.fromFile(_head);
    }

}
