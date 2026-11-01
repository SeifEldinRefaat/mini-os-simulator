import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    Queue<Process> blockedProcesses;
    Queue<Process> readyProcesses;
    public static int time;

    public Scheduler(){
        blockedProcesses = new LinkedList<>();
        readyProcesses = new LinkedList<>();
    }



//    public void scheduling () throws IOException {
//        boolean flag = false;
//        Process s;
//        while (time < 10){
//            if (flag == false) {
//                s = readyProcesses.remove();
//            }
//            File f = new File(s.code);
//            FileReader fr = new FileReader(f);
//            BufferedReader br = new BufferedReader(fr);
//            executeSystemCall(br.readLine());
//            executeSystemCall(br.readLine());
//            if (readyProcesses.isEmpty()==False) {
//                readyProcesses.add(s);
//                flag=false;
//            }
//            else
//                flag =True;
//            time++;
//        }



}
