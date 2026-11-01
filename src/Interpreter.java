import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Loads programs as processes and runs them under the round-robin scheduler,
 * resolving each instruction against the running process's variables and the
 * three system mutexes.
 */
public class Interpreter {
    SystemCall systemCall;
    static Memory memory;
    static Scheduler scheduler;
    Map<String, Mutex> mutexes;
    private int nextProcessID = 1;
    // Programs waiting for their arrival time, as {arrivalTime, path}.
    private final List<Object[]> pending = new ArrayList<>();

    public Interpreter() throws FileNotFoundException {
        this(2);
    }

    public Interpreter(int quantum) throws FileNotFoundException {
        systemCall = new SystemCall();
        memory = new Memory();
        scheduler = new Scheduler(quantum);
        mutexes = new LinkedHashMap<>();
        for (String name : new String[]{"userInput", "userOutput", "file"})
            mutexes.put(name, new Mutex(name));
    }

    /** Registers a program to be admitted once the clock reaches arrivalTime. */
    public void scheduleArrival(String path, int arrivalTime) {
        pending.add(new Object[]{arrivalTime, path});
    }

    /** Admits every pending program whose arrival time has been reached. */
    private void admitArrivals() throws FileNotFoundException {
        pending.sort((a, b) -> Integer.compare((Integer) a[0], (Integer) b[0]));
        while (!pending.isEmpty() && (Integer) pending.get(0)[0] <= Scheduler.time) {
            Object[] arrival = pending.remove(0);
            loadProcess((String) arrival[1]);
        }
    }

    /** Reads a program file and admits it to the ready queue as a new process. */
    public Process loadProcess(String path) throws FileNotFoundException {
        List<String> instructions = new ArrayList<>();
        try (Scanner reader = new Scanner(new File(path))) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (!line.isEmpty()) instructions.add(line);
            }
        }
        Process p = new Process(nextProcessID++, instructions);
        scheduler.admit(p);
        System.out.println("[load] " + p + " <- " + path + " (" + instructions.size() + " instructions)");
        return p;
    }

    /** Runs until every program has arrived, been admitted and finished. */
    public void run() throws IOException {
        admitArrivals();
        while (scheduler.hasWork() || !pending.isEmpty()) {
            admitArrivals();
            Process p = scheduler.nextReady();
            if (p == null) {        // nothing ready yet; advance the clock to the next arrival
                Scheduler.time++;
                continue;
            }
            System.out.println("[dispatch] " + p + " at t=" + Scheduler.time);
            printQueues();

            for (int executed = 0; executed < scheduler.quantum; executed++) {
                if (!p.hasNextInstruction()) {
                    p.pcb.setProcessState(ProcessState.FINISHED);
                    System.out.println("[finish] " + p);
                    break;
                }
                String instruction = p.nextInstruction();
                p.advance();
                Scheduler.time++;

                boolean blocked = execute(p, instruction);
                if (blocked) {
                    scheduler.block(p);
                    System.out.println("[block] " + p + " on " + instruction);
                    printQueues();
                    break;
                }
                admitArrivals();
            }

            if (p.pcb.getProcessState() == ProcessState.RUNNING) {
                if (p.hasNextInstruction()) scheduler.requeue(p);
                else {
                    p.pcb.setProcessState(ProcessState.FINISHED);
                    System.out.println("[finish] " + p);
                }
            }
        }
        System.out.println("[done] all processes finished at t=" + Scheduler.time);
    }

    /** The brief asks for the queues after each scheduling event. */
    private void printQueues() {
        System.out.println("          ready=" + scheduler.readyProcesses
                + " blocked=" + scheduler.blockedProcesses);
    }

    /** Executes one instruction. Returns true if the process became blocked. */
    private boolean execute(Process p, String instruction) throws IOException {
        String[] parts = instruction.split("\s+");
        switch (parts[0]) {
            case "semWait": {
                Mutex m = mutex(parts[1]);
                return !m.semWait(p);
            }
            case "semSignal": {
                Mutex m = mutex(parts[1]);
                Process unblocked = m.semSignal(p);
                if (unblocked != null) {
                    scheduler.unblock(unblocked);
                    System.out.println("[unblock] " + unblocked + " now owns " + m.name);
                }
                return false;
            }
            case "assign": {
                String var = parts[1];
                if (parts[2].equals("input")) {
                    System.out.print("[" + p + "] enter a value for " + var + ": ");
                    System.out.flush();
                    p.setVariable(var, parse(SystemCall.readUser()));
                } else if (parts[2].equals("readFile")) {
                    String path = String.valueOf(p.getVariable(parts[3]));
                    p.setVariable(var, SystemCall.readFile(path));
                } else {
                    p.setVariable(var, parse(parts[2]));
                }
                return false;
            }
            case "print": {
                SystemCall.printOnScreen(String.valueOf(p.getVariable(parts[1])));
                return false;
            }
            case "printFromTo": {
                int from = toInt(p.getVariable(parts[1]));
                int to = toInt(p.getVariable(parts[2]));
                StringBuilder sb = new StringBuilder();
                for (int i = from; i <= to; i++) sb.append(i).append(i < to ? " " : "");
                SystemCall.printOnScreen(sb.toString());
                return false;
            }
            case "writeFile": {
                String path = String.valueOf(p.getVariable(parts[1]));
                String data = String.valueOf(p.getVariable(parts[2]));
                SystemCall.writeFile(path, data);
                System.out.println("[io] " + p + " wrote to " + path);
                return false;
            }
            default:
                throw new IOException("Unknown instruction: " + instruction);
        }
    }

    private Mutex mutex(String name) throws IOException {
        Mutex m = mutexes.get(name);
        if (m == null) throw new IOException("Unknown mutex: " + name);
        return m;
    }

    private static Object parse(String token) {
        return token.chars().allMatch(Character::isDigit) && !token.isEmpty()
                ? Integer.valueOf(token) : token;
    }

    private static int toInt(Object value) throws IOException {
        if (value instanceof Integer) return (Integer) value;
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            throw new IOException("Expected a number but got: " + value);
        }
    }
}
