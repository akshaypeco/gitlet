# Gitlet Design Document

**Akshay Patel**:

## Classes and Data Structures

Blob

    Blob contains the contents of a file. It takes in
    the File object, and creates instance attributes
    of NAME and CODE. It will be used later as a 
    serialized file under a BLOBS directory. It will 
    be named using the hashcode we're using in 
    this project. A NAME instance variable will refer to the actual
    name of the file, in order to compare specific files later. 
    To create a blob, the user ADDS a file to the Gitlet directory. 
    This blob will be pointed to in a COMMIT object, and will 
    represent the information needed for a successful implementation 
    of the add command and others.



Commit

    Class that creates commit objects. Commits 
    have pointers to blobs. These blobs never change so 
    I will be able to go back to this specific blob if necessary 
    based on the information stored in commit. One commit will always
    be the HEAD commit, and the original commit will be the PARENT.
    Each commit in the LinkedList will point towards the next one. Therefore,
    to retrieve a previous commit, I'll cycle through the commits before the HEAD,
    until I find the specified commit tag. Then, I'll reinstantiate the 
    blobs in the specified commit file, and leave the blobs that aren't 
    affected. 
    



WorkingTree

    Class that contains the LinkedLists of Commit 
    objects. If more than working tree exists, another 
    file will be created. Otherwise, everything will be 
    stored in one WorkingTree object called MASTER. This WorkingTree object 
    will be accessed through Main. The current WorkingTree will be 
    kept as information in the Main class as a variable. When merge
    is called, I will combine both working tree's data into one working
    tree, and then only one file will remain in the variable. The variable
    will be an array list of WorkingTree files. The file will be named by 
    an arbitrary name but I might change this later if necessary.
    
    
    


## Algorithms

    I'll be using hashcodes to determine blob
    file names and if files are holding the same information.
    This hash function is already included, and it has almost an
    impossible chance of collision with other file objects. Therefore,
    I'll be able to determine the uniqueness of a blob by just it's name.
    I can do this through an equals function or simply by just accessing
    the NAME instance variable of a blob. 

    Commits will be numbered based on a unique commit ID. I still
    need to research how I'll be generating this, and if this functionality
    is provided by the staff. 



## Persistence
 
    /.gitlet

    The main directory for Gitlet. This will be initialized 
    with the INIT command. Everything in this project will
    be under this directory, and it will never be deleted.

    /WorkingTree 
        
        Files for all WorkingTrees. Will merge 
        using files in this directory. WorkingTrees will
        never be deleted, but a variable in MAIN will determine
        which trees will ever be touched and/or merged.

        /Blobs
        
            Where all the unique blob files are stored. The blobs
            are never deleted but will can be pointed to by multiple
            COMMITS.

        /Commits

            Where all the unique commit files are stored. Commit files are
            never deleted and each have a code so that it can restore edited
            files to their previous state and leave new files untouched.

    
        





