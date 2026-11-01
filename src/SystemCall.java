import jdk.jshell.execution.Util;

import java.io.*;
import java.util.Scanner;

public class SystemCall {
    //String type;

    public SystemCall() {

    }

    public static String readFile(String path) throws IOException {
        File f = new File(path);
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        return br.readLine();
    }

    public static void writeFile(String path, String data) throws IOException {
        File f = new File(path);
        FileWriter fw = new FileWriter(f, true);
        fw.write(data);
        fw.write('\n');
    }

    public static void printOnScreen(String s){
        System.out.println(s);
    }

    public static String readUser(){
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    public void readMemory(){

    }

    public void writeMemory(){

    }

//    public static boolean isNumeric(String str) {
//        try {
//            Integer.parseInt(str);
//            return true;
//        } catch(NumberFormatException e){
//            return false;
//        }
//    }
    public static Word executeSystemCall(String call, int processID) throws IOException {
        String[] parts = call.split(" ");
        Word word1 = null;
        switch (parts[0]) {
            case "print":
                printOnScreen(parts[1]);
                break;
            case "assign":
                Object data;
                String Vname = parts[1];
                if (parts[2].equals("input")) {
                    System.out.println("Please enter a value");
                    data = readUser();
                } else if (parts[2].equals("readFile")) {
                    data = null; //to be executed by readFile

                } else {
                    boolean isNumeric = parts[2].chars().allMatch(Character::isDigit);
                    if (isNumeric) {
                        data = Integer.parseInt(parts[2]);
                    } else {
                        data = parts[2];
                    }
                }
                word1 = new Word(Vname, data);
                break;
            case "writeFile", "readFile":
                //writeFile(); To be executed by Daddy Hazem Sherif
                break;
            //            else{
//                data = call.substring(9,call.length());
//            }
//        }
//        if (call.startsWith("readFile")){
//            readFile(call.substring(9,call.length()));
//        }
//        if(call.startsWith("writeFile")){
//            writeFile(call.substring(10,11),call.substring(12,call.length()));
//        }
            case "printFromTo":
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                for (int i = x; i < y + 1; ++i) {
                    System.out.print(i);
                }
                //        }

//        if(call.startsWith("semSignal")){
//
                break;
            case "semWait", "semSignal":


                break;
        }
        return word1;
    }

}

