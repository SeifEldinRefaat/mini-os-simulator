import java.util.ArrayList;
import java.util.List;

public class Process {
    PCB pcb;
    // The three variable slots the memory model allows each process.
    Word word1;
    Word word2;
    Word word3;
    List<String> instructions;

    public Process(int processID, List<String> instructions) {
        this.pcb = new PCB();
        this.pcb.setProcessID(processID);
        this.pcb.setProcessState(ProcessState.NEW);
        this.pcb.setProgramCounter(0);
        this.instructions = instructions == null ? new ArrayList<>() : instructions;
    }

    boolean hasNextInstruction() {
        return pcb.getProgramCounter() < instructions.size();
    }

    String nextInstruction() {
        return instructions.get(pcb.getProgramCounter());
    }

    void advance() {
        pcb.setProgramCounter(pcb.getProgramCounter() + 1);
    }

    void setVariable(String var, Object data) {
        if (word1 == null || word1.var.equals(var)) { word1 = new Word(var, data); return; }
        if (word2 == null || word2.var.equals(var)) { word2 = new Word(var, data); return; }
        word3 = new Word(var, data);
    }

    Object getVariable(String var) {
        for (Word w : new Word[]{word1, word2, word3})
            if (w != null && w.var.equals(var)) return w.data;
        return null;
    }

    @Override
    public String toString() {
        return "P" + pcb.getProcessID();
    }
}
