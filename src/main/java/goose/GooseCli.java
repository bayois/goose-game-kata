package goose;

import java.io.Console;

/*
* Gose Game: Console handler
* read user input command and reply 
*/
public class GooseCli{
    Console cnsl = null;

    void GoseCli(){
        // creates a console object
        cnsl = System.console();
    }

    // Write text and Read command line
    String readCommand(String outputText) {
        String cmd = null;
        try {
            write(outputText);
            cmd = System.console().readLine();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cmd;
    }

    // Write command line
    void write(String outputText){
        System.out.print(outputText);
    }


}