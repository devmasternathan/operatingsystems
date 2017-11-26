/**
 **@author Ryan Bassit
 **@author Mohammed Sujon
 **@author Nathan Blanchard
 **<h1> FreeSpace Class <h1>
 **<p>Desciption : This class represent the manipulation of memory (such as finding a free space,
 ** removing a job from memory, and adding a job to memory).</p>
 **/


import java.util.ArrayList;

public class FreeSpace {

    private ArrayList<Integer> memory;

    public FreeSpace(){  //Initializing the memory.
        memory = new ArrayList<>();
        for (int i = 0; i < Static.MAX_WORD; i++){ //Create 100K  of memory.
            memory.add(0);
        }
    }

    /*This method finds the best fit free space in memory.
    Creates variables to store the beginning, and end of freespace chunk.
    Then traverses through to memory to find the first freespace.
    Then checks to make sure the freespace is adequate enough for the job.
    */
    public int allocateFreeSpace(int jobSize){
        int memoryChunk = Static.SENTINEL; // memoryChunk is the variable that will hold the address of the freespace will best fit our job.
        boolean foundFreeSpaceIndex = false;
        int beginningFreespace = Static.SENTINEL; // stores the address of a free memory chunk.
        int endAddress = 0; // stores the ending address of the free memory chunk.
        int freeSpaceLeft = 100; // Initialized to 100. Will store the free space that's left.

        for(int i = 0; i< Static.MAX_WORD; i++){ // traverses through memory
            if(memory.get(i)==0 && foundFreeSpaceIndex==false){ // finds first available free space address in memory for the job.
                beginningFreespace = i; //saves the index of the beginning  of the freespace.
                foundFreeSpaceIndex = true; //Flag to see if freespace has been found.
            }

            if((memory.get(i)==1 && foundFreeSpaceIndex) || (i == 99 && foundFreeSpaceIndex)){//find end of free space
                if(i==99)
                    endAddress = i+1; //used to indicate if memory is full.
                else
                    endAddress = i;

                if(endAddress-beginningFreespace>=jobSize)
                {//determine if the freespace left is the correct size for the job.
                    memoryChunk = beginningFreespace; //assign the address of the freespace to the memoryChunk variable.
                    foundFreeSpaceIndex = false;
                    if(endAddress-beginningFreespace < freeSpaceLeft){ //is new free space the smallest space needed for a job
                        freeSpaceLeft = endAddress-beginningFreespace; //Find the the free space left.//
                    }
                }
                else
                    foundFreeSpaceIndex = false; // if the the job is not the correct size the space has not found.
            }
        }
        return memoryChunk; // starting address of free space that is best fit for the job.
    }

    //      **********addToMemory**********
    // This method accepts the job size and fills up our memory chunk with the size of the current job.
    // It does this by setting the bit to 1 in memory.
    public int addToMemory(int jobSize){
        //fills up the free space
        int memoryChunk = allocateFreeSpace(jobSize); //find available free space for current job using the job's size.
        if(memoryChunk!= Static.SENTINEL){ // if we found space in memory.
            for(int i = memoryChunk; i<memoryChunk+jobSize; i++) //loops through the memory chunk.
                memory.set(i, 1); //sets each index to 1.
            return memoryChunk; // return memory chunk
        }
        return Static.SENTINEL; //return -1 if no memory available
    }

    //      **********removeFromMemory**********
    // This method accepts a jobs start address in memory and size.
    // Then we loop through memory set the bit to 0 (removing it from memory).
    public void removeFromMemory(int startAddress, int jobSize){
        for(int i = startAddress; i < startAddress+jobSize; i++)
            memory.set(i, 0);
    }
}
