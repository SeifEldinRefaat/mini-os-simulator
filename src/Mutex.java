import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
   Value value;
   int ownerID;
   Queue <String> blocked;

   public Mutex(){
      blocked = new LinkedList<>();
   }

   public void semWait(Process p){
      if(value == Value.One){
         value = Value.Zero;
         ownerID = p.pcb.getProcessID();
      }else{
         blocked.add(p);
         //add to schedular Blocked queue
         p.pcb.setProcessState(ProcessState.BLOCKED);
      }
   }
   public void semSignal(Process p){
      if(ownerID == p.pcb.getProcessID()){
         if(blocked.isEmpty()){
            value = Value.One;
         }else{
            Process unblockedP = blocked.remove();
            //remove from schedular Blocked queue and add to ready queue
            ownerID = unblockedP.pcb.getProcessID();
            unblockedP.pcb.setProcessState(ProcessState.READY);
         }
      }
   }
}
