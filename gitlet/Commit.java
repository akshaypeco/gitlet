package gitlet;

import java.io.File;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.ArrayList;


/** Commit class that stores information of which Blobs are tracked.
 *  @author Akshay Patel*/
public class Commit implements Serializable {

    /** Message that accompanies a Commit command. */
    private String _message;

    /** Commit folder. */
    static final File COMMIT_FOLDER = Utils.join(Main.GITLET_FOLDER, "commits");

    /** SHA-1 of Commit. */
    private String _codeName;

    /** Parent of this Commit. */
    private Commit _parent;

    /** Time of Commit. */
    private String _date;

    /** TreeMap with key of name of file and value
     * of corresponding Blob file code. */
    private TreeMap<String, String> _namesAndCodes;

    /** Commit object initializer.
     * @param message
     * Message of commit
     * @param parent
     * parent commit */
    public Commit(String message, Commit parent) {
        if (parent == null) {
            _date = "Wed Dec 31 16:00:00 1969 -0800";
        } else {
            _date = getTime();
        }
        if (parent != null) {
            _namesAndCodes = parent.getBlobMap();
        } else {
            _namesAndCodes = new TreeMap<String, String>();
        }
        _parent = parent;
        _message = message;
        _codeName = null;
    }

    /** Gets the SHA1 code of commit.
     * @return SHA1. */
    public String getSixSHA1() {
        String sixChars = "";
        for (int i = 0; i < 8; i += 1) {
            sixChars += _codeName.charAt(i);
        }
        return sixChars;
    }

    /** Gets the parent Commit of commit.
     * @return parent Commit. */
    public Commit getParent() {
        return _parent;
    }

    /** Gets the message of commit.
     * @return message. */
    public String getMessage() {
        return _message;
    }

    /** Gets time of commit.
     * @return time. */
    private String getTime() {
        String output = DateTimeFormatter
                .RFC_1123_DATE_TIME.format(ZonedDateTime.now());
        String[] splitOutput = output.split(",");
        output = splitOutput[0] + splitOutput[1];
        splitOutput = output.split(" ");
        return splitOutput[0] + " " + splitOutput[2]
                + " " + splitOutput[1] + " " + splitOutput[4]
                + " " + splitOutput[3] + " " + splitOutput[5];
    }

    /** Adds Blob object to a list of strings
     * indicating the Commit's blob files.
     * @param blob */
    public void addBlob(Blob blob) {
        _namesAndCodes.put(blob.getFileName(), blob.getBlobSHA1());
    }

    /** Removes blob from the TreeMap of Blobs.
     * @param file */
    public void removeBlob(String file) {
        _namesAndCodes.remove(file);
    }

    /** Returns a TreeMap of files of Blobs in
     * the form of strings of their file names and SHA-1 codes. */
    public TreeMap<String, String> getBlobMap() {
        return _namesAndCodes;
    }

    /** Clone the specified Commit into this commit.
     * @param commit */
    public void cloneBlob(Commit commit) {
        _namesAndCodes.putAll(commit.getBlobMap());
    }

    /** Returns the SHA1 of the commit. */
    public String getCodeName() {
        return _codeName;
    }

    /** Gets blob from commit.
     * @param fileName
     * File name of blob
     * @return Blob */
    public Blob getBlob(String fileName) {
        return Blob.fromFile(_namesAndCodes.get(fileName));
    }

    /** Gets log message of commit.
     * @return Log of commit */
    public String getLog() {
        return "===" + "\n"
                + "commit " + _codeName + "\n"
                + "Date: " + _date + "\n"
                + _message + "\n";
    }

    /** Sets the codename for the Commit.
     * @return SHA1 code for Commit. */
    private String setCodeName() {
        if (_parent == null) {
            return Utils.sha1(_message + _date);
        }
        String blobs = "";
        ArrayList<String> keySet =
                new ArrayList<String>(_namesAndCodes.keySet());
        for (String blob : keySet) {
            blobs += _namesAndCodes.get(blob);
        }
        return Utils.sha1(_message + _date + _parent.getCodeName() + blobs);
    }


    /** Saves Commit for future use in COMMIT folder. */
    public void saveCommit() {
        _codeName = setCodeName();
        Utils.writeObject(Utils.join(COMMIT_FOLDER, _codeName), this);
    }

    /** Retrieves and deserializes Commit object from Commit file name CODENAME
     * in COMMIT folder.
     * @return Commit */
    public static Commit fromFile(String codeName) {
        return Utils.readObject
                (Utils.join(COMMIT_FOLDER, codeName), Commit.class);
    }

}
