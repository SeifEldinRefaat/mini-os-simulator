import java.io.*;
import java.util.Scanner;

/**
 * The I/O primitives available to running processes. Instruction semantics live
 * in Interpreter; this class only performs the underlying operations.
 */
public class SystemCall {

    // One shared Scanner: constructing a new one per read and letting it go out
    // of scope can close System.in for every later read.
    private static final Scanner STDIN = new Scanner(System.in);

    public SystemCall() {
    }

    /** Reads the whole file, not just its first line, so a multi-line file prints in full. */
    public static String readFile(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (sb.length() > 0) sb.append(System.lineSeparator());
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public static void writeFile(String path, String data) throws IOException {
        // try-with-resources: the original never closed the writer, so data could
        // stay buffered and never reach disk. Overwrites rather than appends, as
        // the brief specifies the file is always created.
        try (FileWriter fw = new FileWriter(new File(path), false)) {
            fw.write(data);
            fw.write('\n');
        }
    }

    public static void printOnScreen(String s) {
        System.out.println(s);
    }

    public static String readUser() {
        return STDIN.hasNextLine() ? STDIN.nextLine() : "";
    }
}
