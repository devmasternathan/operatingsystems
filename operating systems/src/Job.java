/**
  **@author Ryan Bassit
  **@author Mohammed Sujon
  **@author Nathan Blanchard
  **<h1> Job Class <h1>
  **<p>Desciption : This class represents the information, and operations performed on a single job.</p>
**/

public class Job {
    private int jobNumber;
    private int jobPriority;
    private int jobSize;
    private int maxCPUtime;
    private int beginCPUTime;
    private int memoryAddress;

    private boolean inMemory;
    private boolean doingIO;
    private boolean swap;
    private boolean block;
    private boolean processing;
    private boolean terminated;
    private boolean requestIO;
    
    public Job(int num, int priority, int size, int time, int currTime){ //Overloaded constructor
        jobNumber = num;
        jobPriority = priority;
        jobSize = size; 
        maxCPUtime = time;
        beginCPUTime = currTime;
        memoryAddress = Static.SENTINEL;
    }

   //Setters
    public void setMaxCPUtime(int maxCPUtime){
        this.maxCPUtime = maxCPUtime;
    }
    public void setBeginCPUTime(int beginCPUTime){
        this.beginCPUTime = beginCPUTime;
    }
    public void setMemoryAddress(int memoryAddress){
        this.memoryAddress = memoryAddress;
    }

    public void setInMemory(boolean inMemory){
        this.inMemory = inMemory;
    }
    public void setDoingIO(boolean doingIO){
        this.doingIO = doingIO;
    }
    public void setSwap(boolean swap){
        this.swap = swap;
    }
    public void setBlock(boolean block){
        this.block = block;
    }
    public void setProcessing(boolean processing){
        this.processing = processing;
    }
    public void setTerminated(boolean terminated){
        this.terminated = terminated;
    }
    public void setRequestIO(boolean requestIO){
        this.requestIO = requestIO;
    }

    //Getters
    public int getJobNumber(){
        return jobNumber;
    }
    public int getJobSize(){
        return jobSize;
    }
    public int getMaxCPUtime(){
        return maxCPUtime;
    }
    public int getBeginCPUTime(){
        return beginCPUTime;
    }
    public int getMemoryAddress(){
        return memoryAddress;
    }

    public boolean getInMemory(){
        return inMemory;
    }
    public boolean getDoingIO(){
        return doingIO;
    }
    public boolean getSwap(){
        return swap;
    }
    public boolean getBlock(){
        return block;
    }
    public boolean getProcessing(){
        return processing;
    }
    public boolean getTerminated(){
        return terminated;
    }
    public boolean getRequestIO(){
        return requestIO;
    }
}
