/**
 **@author Ryan Bassit
 **@author Mohammed Sujon
 **@author Nathan Blanchard
 **<h1> OS Class <h1>
 **<p>Desciption : The project is a simulation of an operating system. Our program works together with an existing
  program (SOS) that simulates a job stream. SOS feeds our program a series of jobs and job requests;
  like a real operating system. Our program delegates those requests and handles the life cycle of each of
  the jobs, from system entry to termination. </p>
 **/
 //hi

import java.util.*;

public class os {

    //      **********startUp**********
    //This method initializes our job table, memory, and IOQueue.
    public static void startup (){
        Static.jobTable = new ArrayList<>();
        Static.memory = new FreeSpace();
        Static.IOQueue = new ArrayList<>();
    }

    //      **********Crint**********
    //Crint is called when a new job enters the system. Accepts array parameters 'a', and 'p'.
    public static void Crint(int [] a, int [] p){
        bookkeeper(p[5]);
        Static.jobTable.add(new Job(p[1],p[2],p[3],p[4],p[5]));
        runJob(a,p); //a is the the state of the system, p stores the job information.
                     //p[1]: job id, p[2]: priority, p[3]: job size, p[4]: maxCPU time, p[5] current cpu time.
    }

    /*				DSKINT:
     * 1)Finds which job is doing IO
     * 2)Goes to job table, and with that position it makes sure the job
     * is not doing IO, not blocked, and not requesting IO.
     * 3)If the job gets killed, terminate that position (job)
     * 4)Then you can run IO and the Job
     */

    /*Invoked when the disk has completed processing I/O(an I/O transfer between disk and memory has been completed).
      First it saves the state of the current running job.
      Then it finds a job to do I/O. If its not doing IO, terminated then it runs the job.
    */

    public static void Dskint(int [] a, int[] p){
        bookkeeper(p[5]);
        Static.ProcessingIO = false;
        int posOfIOJob = findJobDoingIO();

        Static.jobTable.get(posOfIOJob).setDoingIO(false);
        Static.jobTable.get(posOfIOJob).setBlock(false);
        Static.jobTable.get(posOfIOJob).setRequestIO(false);

        if(Static.jobTable.get(posOfIOJob).getTerminated())
        {
            terminateJob(posOfIOJob);
        }
        runIO();
        runJob(a,p);
    }

    /*					DRMINT:
     * 1)Checks to see if there is something swapping in.
     * 2)If there is then you basically go through the job table, find what is
     * getting swapped. You make sure that its not setting a swap and that its
     * set in memory. As soon as you do that you can leave the loop.
     * 3)Checks to see if there is something swapping out as well.
     * 4)Basically you go through the same list, and find what is getting swapped
     * out. Make sure that job is not setting a swap and is not set in memory. You can
     * leave the loop as soon as you find that job.
     * 5)At the end just run the jobs again.
     */
    public static void Drmint(int[] a, int[] p){
        bookkeeper(p[5]);
        if(Static.inSwapping){
            Static.inSwapping = false;
            for(int i = 0; i< Static.jobTable.size(); i++){
                if(Static.jobTable.get(i).getSwap()){
                    Static.jobTable.get(i).setSwap(false);
                    Static.jobTable.get(i).setInMemory(true);
                    break;
                }
            }
        }
        if(Static.outSwapping){
            Static.outSwapping = false;
            for(int i = 0; i< Static.jobTable.size(); i++){
                if(Static.jobTable.get(i).getSwap()){
                    Static.jobTable.get(i).setSwap(false);
                    Static.jobTable.get(i).setInMemory(false);
                    break;
                }
            }
        }
        runJob(a,p);
    }

    //      **********TRO**********
    //Invoked when IntervalTimer's register has decremented to 0.
    //Bookkeep job to update its current time (p[5]).
    public static void Tro(int[] a, int [] p){
        int jobTerminated = bookkeeper(p[5]); // bookkeep job and set jobTerminated to the index of current running job.
        if(Static.jobTable.get(jobTerminated).getMaxCPUtime() == 0) // check if jobs max cpu time left is 0.
            //if true, check if the job is doing or requesting I/O.
            if(Static.jobTable.get(jobTerminated).getDoingIO() || Static.jobTable.get(jobTerminated).getRequestIO())
                Static.jobTable.get(jobTerminated).setTerminated(true); // terminate the job if either condition is true.
            else {
                terminateJob(jobTerminated); // remove the job from memory.
            }

        runJob(a,p); //continue servicing other jobs.
    }
    /*	     **********SVC**********
     Handles service request for various jobs. It handles termination, disk I/O, and request to be blocked,
     until all current I/O is finished. Handles termination by killing a job if it's requesting/doing I/O.
     It hands disk I/O requests by adding the I/O queue. Checks the I/O queue, if only one jobs left then after
     that job is completed the current job can be run. The third request to be blocked is handled by blocking the job if
     it requesting/doing I/O.
     */
    public static void Svc (int[] a, int [] p){
        int jobTerminated = bookkeeper(p[5]);
        if (a[0] == 5){

            if(Static.jobTable.get(jobTerminated).getDoingIO() || Static.jobTable.get(jobTerminated).getRequestIO())
                Static.jobTable.get(jobTerminated).setTerminated(true);
            else{
                terminateJob(jobTerminated);
            }
        }
        if(a[0] == 6){
            Static.jobTable.get(jobTerminated).setRequestIO(true);
            Static.IOQueue.add(Static.jobTable.get(jobTerminated).getJobNumber());
            if(Static.IOQueue.size()==1 && !Static.ProcessingIO){
                runIO();
            }
        }
        if(a[0] == 7){
            if(Static.jobTable.get(jobTerminated).getDoingIO() || Static.jobTable.get(jobTerminated).getRequestIO())
                Static.jobTable.get(jobTerminated).setBlock(true);
        }
        runJob(a,p);
    }

    /*					CPU SCHEDULER:
     * This function handles which jobs to schedule using Round Robin.
     * 1)If there is no more jobs in the job table, don't schedule.
     * 2)If runnableJobIndex is greater than or equal to the job table's size, reset runnableJobIndex to 0
     * 3)If there are jobs that are in memory, not getting blocked or not getting killed
     * then return runnableJobIndex(this value will eventually be stored into the function to control
     * running jobs.
     */
    public static int CPUScheduler(){
        if(Static.jobTable.size() == 0)
            return -1;
        if(Static.runnableJobIndex >= Static.jobTable.size())
            Static.runnableJobIndex = 0; //runnableJobIndex starts from beginning if jobtable size shrinked
        int end = Static.runnableJobIndex; // will prevent us from running in a circul

        while(true)
        {
            if(Static.jobTable.get(Static.runnableJobIndex).getInMemory() && !Static.jobTable.get(Static.runnableJobIndex).getBlock() && !Static.jobTable.get(Static.runnableJobIndex).getTerminated()){
                return Static.runnableJobIndex;
            }
            Static.runnableJobIndex++;
            if(Static.runnableJobIndex == Static.jobTable.size())// if get to the end of Static.jobTable start over
                Static.runnableJobIndex =0;
            if(Static.runnableJobIndex == end)//if get to the point where start get out from the loop
                break;
        }
        return -1;
    }
    /*	     **********isAllMemoryBlocked**********
     Function checks to see if all jobs are blocked, by comparing the total number of blocked jobs,
     and the total number of jobs in memory.
     */
    public static boolean isAllMemoryBlocked(){
        int blocks = 0;
        int mem = 0;
        for(int i = 0; i< Static.jobTable.size(); i++){
            if(Static.jobTable.get(i).getInMemory() && Static.jobTable.get(i).getBlock()){
                blocks++;
            }
            if(Static.jobTable.get(i).getInMemory())
                mem++;
        }
        if (blocks == mem)
            return true;
        return false;
    }
    /*					SWAPPER:
     * This function controls swapping.
     * 1)If nothing is getting swapped in or out.
     * 	-Go through the whole job table, starting from the end.
     * 	-If there is a job in the table that is not in memory, store the address
     * 	into a variable. To set the variable take the memory and add it to the job size
     *  and that is your address
     * 2)If it found a job thats in memory
     * 	-Go into the table and get that job.
     * 	-Swap it into the drum.
     * 3)If it didn't find a job in memory and memory is blocked
     * 	-You go through the IO Queue starting from the end
     * 	-If there is a job in the table that is not getting blocked and not doing IO
     * 	and its in the memory, swap it out of the drum.
     * 	-Remove it from memory
     */
    public static void swapper(){

        int intialIndexofMemoryChunk = -1;
        if(!Static.inSwapping && !Static.outSwapping){ //Check to make to sure that nothing is being swapped.

            for(/*int i = Static.jobTable.intialIndexofMemoryChunk()-1; i>=0; i--*/int i = 0; i < Static.jobTable.size(); i++){ //Loop through the job table.
                if(!Static.jobTable.get(i).getInMemory()){ //Find the first occurance of a job that's not in memory.
                    intialIndexofMemoryChunk = Static.memory.addToMemory(Static.jobTable.get(i).getJobSize()); //Set intialIndexofMemoryChunk of the job.
                    if(intialIndexofMemoryChunk != -1){ //If there is no job
                        Static.jobTable.get(i).setMemoryAddress(intialIndexofMemoryChunk); //Set the mem
                        Static.jobTable.get(i).setSwap(true);
                        sos.siodrum(Static.jobTable.get(i).getJobNumber(), Static.jobTable.get(i).getJobSize(), Static.jobTable.get(i).getMemoryAddress(), 0);
                        Static.inSwapping = true;
                        break;
                    }
                }
            }
            if(intialIndexofMemoryChunk == -1 && isAllMemoryBlocked()){

                for(/*int i = Static.IOQueue.intialIndexofMemoryChunk()-1; i>=0; i--*/int i = 0; i < Static.IOQueue.size(); i++){
                    int index = findJobNumber(Static.IOQueue.get(i));
                    if(Static.jobTable.get(index).getBlock() && !Static.jobTable.get(index).getDoingIO() && Static.jobTable.get(index).getInMemory()){
                        Static.jobTable.get(index).setSwap(true);
                        sos.siodrum(Static.jobTable.get(index).getJobNumber(), Static.jobTable.get(index).getJobSize(), Static.jobTable.get(index).getMemoryAddress(), 1);
                        Static.outSwapping = true;
                        Static.memory.removeFromMemory(Static.jobTable.get(index).getMemoryAddress(), Static.jobTable.get(index).getJobSize());
                        break;
                    }
                }
            }
        }
    }
     /*	     **********runJob**********
       Gets job from CPU Scheduler, and then run I/O and swapper. Depending of the CPU scheduler
       it will either run the job using the time slice(If there is something to run) or run no jobs(If there
       are no jobs to run).
     */
    public static void runJob(int [] a, int [] p){

        int TIME_SLICE = 50;
        int jobToRun = CPUScheduler();
        if(jobToRun==-1)
            a[0] = 1;
        else{
            if(TIME_SLICE >= Static.jobTable.get(jobToRun).getMaxCPUtime()){
                TIME_SLICE = Static.jobTable.get(jobToRun).getMaxCPUtime();
            }
            a[0]=2;
            p[2] = Static.jobTable.get(jobToRun).getMemoryAddress();
            p[3] = Static.jobTable.get(jobToRun).getJobSize();
            p[4] = TIME_SLICE;
            Static.jobTable.get(jobToRun).setBeginCPUTime(p[5]);
            Static.jobTable.get(jobToRun).setProcessing(true);
        }

        runIO();
        swapper();
    }

    //      **********bookkeeper**********
    // This method saves information about the current job, so we do not lose data.  Accepts an int current time.
    public static int bookkeeper(int currentTime){
        int runningJob  = findRunningJob(); // find the job that is currently running on CPU.
        if(runningJob!=-1){ // if we found a job.
            Static.jobTable.get(runningJob).setProcessing(false); // set processing to false.
            int timeInCPU = currentTime - Static.jobTable.get(runningJob).getBeginCPUTime(); //how long job was running.
            int timeLeft = Static.jobTable.get(runningJob).getMaxCPUtime() - timeInCPU; // time left for that job to complete.
            Static.jobTable.get(runningJob).setMaxCPUtime(timeLeft); // update the jobs max time needed with time left.
            return runningJob; // return the running jobs data.
        }
        return -1; // if no jobs in system
    }

    //      **********findJobNumber**********
    // This method finds the index of a specific job number in the job table. Accepts an int job number.
    public static int findJobNumber(int jobNumber){
        for(int i = 0; i < Static.jobTable.size(); i++) // loop through job table.
            if(Static.jobTable.get(i).getJobNumber() == jobNumber) // if the job number is found.
                return i; // return the index of that job in the job table.
        return -1; // if no match found.
    }

    /*      **********findRunningJob**********
      Function finds the job that is running on the CPU, by traversing through the job table, and
       by checking if that job is running.
     */
    public static int findRunningJob(){
        for(int i = 0; i < Static.jobTable.size(); i++)
            if(Static.jobTable.get(i).getProcessing())
                return i;
        return -1;
    }

    //      **********findJobDoingIO**********
    // This method return the index of first job that is doing I/O.
    public static int findJobDoingIO(){
        for(int i = 0; i< Static.jobTable.size(); i++) // loop through the job table.
            if(Static.jobTable.get(i).getDoingIO()) // check if a job is doing I/O.
                return i; // return index of that job.
        return -1; // return -1 if no jobs found doing I/O.
    }

    /*
     * 				runIO:
     */
    public static void runIO(){
        if(!Static.ProcessingIO){
            if (!Static.IOQueue.isEmpty()){
                for(int i = 0; i< Static.IOQueue.size(); i++){
                    int jobPosition = findJobNumber(Static.IOQueue.get(i));
                    if(Static.jobTable.get(jobPosition).getInMemory()){
                        sos.siodisk(Static.IOQueue.get(i));
                        Static.IOQueue.remove(i);
                        Static.jobTable.get(jobPosition).setDoingIO(true);
                        Static.ProcessingIO = true;
                        break;
                    }
                }
            }
        }
    }
    /*				terminateJob:
     * This function removes the job from memory.
     */
    public static void terminateJob(int position){
        Static.memory.removeFromMemory(Static.jobTable.get(position).getMemoryAddress(), Static.jobTable.get(position).getJobSize());
        Static.jobTable.remove(position);
    }
}
