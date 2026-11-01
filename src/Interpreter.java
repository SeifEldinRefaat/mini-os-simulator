import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Interpreter {
    SystemCall systemCall;
    static Memory memory;
    static Scheduler scheduler;
    Mutex mutex;

    public Interpreter() throws FileNotFoundException {
        systemCall = new SystemCall();
        memory = new Memory();
        scheduler = new Scheduler();
        mutex = new Mutex();

    }

    public void loadProcess(String path){
        
    }
}
