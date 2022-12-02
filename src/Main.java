import java.io.*;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        ReadFile readFile = new ReadFile();
        System.out.println("Process started...");
        long startTime = System.currentTimeMillis();
        readFile.readCustomerFile();
        long endTime = System.currentTimeMillis();
        System.out.println("Process ended. It took " + (endTime - startTime) + " milliseconds");
    }
}
