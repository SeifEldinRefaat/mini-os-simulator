import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
public class Memory {

    int processCount;
    int availableSpace;

    final int startInstructions = 21;
    Word[] memory ;

    public Memory() throws FileNotFoundException {
        processCount = 0;
        memory = new Word[40];
        availableSpace = 40 - 7*3;

    }
    public void swap(){

    }
    public void createProcess(int process) throws FileNotFoundException {
        File myObj = new File("inputfiles/Program_" + process + ".txt");
        Scanner myReader = new Scanner(myObj);
        ArrayList<String> instructions = new ArrayList<>();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            instructions.add(data);
        }
        myReader.close();
        int requiredMemory = instructions.size();
        if (availableSpace < instructions.size() + 7) {
            writeOnDisk(process,instructions);
        }else {
            availableSpace = availableSpace - requiredMemory;
            int insert = 0;
            for(int i =0 ; i<40 ; ++i){
                if(memory[i] == null){
                    insert = i;
                    break;
                }
            }
            memory[insert++] = new Word("processID",process);
            memory[insert++] = new Word("programCounter",0);
            memory[insert++] = new Word("programCounter",process);
            memory[insert++] = new Word("programCounter",process);
            for(int i = insert ; i < 40 ; i++){

            }
        }


    }
    public void writeOnDisk(int process , ArrayList<String> instructions) {


        
    }

}
