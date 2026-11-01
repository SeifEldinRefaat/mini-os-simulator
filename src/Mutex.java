import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
   final String name;
   Value value;
   int ownerID;
   Queue<Process> blocked;

   public Mutex(String name) {
      this.name = name;
      this.value = Value.One;   // a mutex starts available
      this.ownerID = -1;
      this.blocked = new LinkedList<>();
   }

   /** Returns true if p acquired the mutex, false if p was blocked on it. */
   public boolean semWait(Process p) {
      if (value == Value.One) {
         value = Value.Zero;
         ownerID = p.pcb.getProcessID();
         return true;
      }
      blocked.add(p);
      p.pcb.setProcessState(ProcessState.BLOCKED);
      return false;
   }

   /**
    * Releases the mutex if p owns it. If another process was waiting, ownership
    * passes directly to it and that process is returned so the caller can move
    * it back to the ready queue; otherwise null.
    */
   public Process semSignal(Process p) {
      if (ownerID != p.pcb.getProcessID()) return null;
      if (blocked.isEmpty()) {
         value = Value.One;
         ownerID = -1;
         return null;
      }
      Process unblockedP = blocked.remove();
      ownerID = unblockedP.pcb.getProcessID();
      unblockedP.pcb.setProcessState(ProcessState.READY);
      return unblockedP;
   }
}
