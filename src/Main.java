import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Interpreter interpreter = new Interpreter(2);

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) interpreter.scheduleArrival(args[i], 0);
        } else {
            // Arrival times from the brief: processes 1, 2 and 3 arrive at t = 0, 1, 4.
            interpreter.scheduleArrival("inputfiles/Program_1.txt", 0);
            interpreter.scheduleArrival("inputfiles/Program_2.txt", 1);
            interpreter.scheduleArrival("inputfiles/Program_3.txt", 4);
        }

        interpreter.run();
    }
}
