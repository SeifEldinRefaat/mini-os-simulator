public class PCB {
    private int processID;
    private ProcessState processState;
    private int programCounter;
    private int memoryBoundaries;

    public PCB(){

    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public ProcessState getProcessState() {
        return processState;
    }

    public void setProcessState(ProcessState processState) {
        this.processState = processState;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getMemoryBoundaries() {
        return memoryBoundaries;
    }

    public void setMemoryBoundaries(int memoryBoundaries) {
        this.memoryBoundaries = memoryBoundaries;
    }
}
