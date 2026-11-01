import java.util.LinkedList;
import java.util.Queue;

/** Round-robin scheduler with a fixed instruction quantum. */
public class Scheduler {
    Queue<Process> blockedProcesses;
    Queue<Process> readyProcesses;
    public static int time;
    final int quantum;

    public Scheduler() {
        this(2);
    }

    public Scheduler(int quantum) {
        this.quantum = quantum;
        this.blockedProcesses = new LinkedList<>();
        this.readyProcesses = new LinkedList<>();
        time = 0;
    }

    void admit(Process p) {
        p.pcb.setProcessState(ProcessState.READY);
        readyProcesses.add(p);
    }

    Process nextReady() {
        Process p = readyProcesses.poll();
        if (p != null) p.pcb.setProcessState(ProcessState.RUNNING);
        return p;
    }

    void requeue(Process p) {
        p.pcb.setProcessState(ProcessState.READY);
        readyProcesses.add(p);
    }

    void block(Process p) {
        blockedProcesses.add(p);
    }

    /** Moves a process the mutex handed ownership to back into the ready queue. */
    void unblock(Process p) {
        blockedProcesses.remove(p);
        requeue(p);
    }

    boolean hasWork() {
        return !readyProcesses.isEmpty();
    }
}
