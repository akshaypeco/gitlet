package gitlet;

import java.io.File;
import java.io.Serializable;

/** Blob class that stores information of a file.
 *  @author Akshay Patel*/
public class Blob implements Serializable {

    /** SHA-1 of Blob. */
    private String _codeName;

    /** Name of Blob file. */
    private String _fileName;

    /** Contents of a file. */
    private byte[] _contents;

    /** Blob folder. */
    static final File BLOB_FOLDER = Utils.join(Main.GITLET_FOLDER, "blobs");

    /** Initializes a blob object with file FILE. */
    public Blob(File file) {
        _codeName = Utils.sha1(file.getName()
                + Utils.readContentsAsString(file));
        _fileName = file.getName();
        _contents = Utils.readContents(file);
    }

    /** Gets contents of Blob.
     * @return contents. */
    public byte[] getContents() {
        return _contents;
    }

    /** Gets SHA1 code of Blob.
     * @return SHA1 code. */
    public String getBlobSHA1() {
        return _codeName;
    }

    /** Gets file name of Blob.
     * @return file name. */
    public String getFileName() {
        return _fileName;
    }

    /** Saves Blob for future use in Blob folder. */
    public void saveBlob() {
        Utils.writeObject(Utils.join(BLOB_FOLDER, _codeName), this);
    }

    /** Retrieves and deserializes Blob object from Blob file name NAME
     * in Blob folder.
     * @return Blob. */
    public static Blob fromFile(String name) {
        return Utils.readObject(Utils.join(BLOB_FOLDER, name), Blob.class);
    }
}
