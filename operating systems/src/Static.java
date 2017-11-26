/**
 **@author Ryan Bassit
 **@author Mohammed Sujon
 **@author Nathan Blanchard
 **<h1> Static Class <h1>
 **<p>Desciption : This class stores all of the neccessary static variables required to run.</p>
 **/

import java.util.ArrayList;

public class Static
{
    //-- flag variable
    static final public int SENTINEL = -1;
    static final public int MAX_WORD= 100; //Memory is 100K, and used in FreeSpace class.

    static int runnableJobIndex; // Get's runnable job from the backing store.

    static boolean inSwapping; //
    static boolean outSwapping;
    static boolean ProcessingIO;

    static FreeSpace memory;

    static ArrayList<Job> jobTable;
    static ArrayList<Integer> IOQueue;
}
