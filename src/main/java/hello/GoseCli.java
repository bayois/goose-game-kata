package hello;

import java.io.Console;

/*
* Gose Game: Console handler
* read user input command and reply 
*/
public class GoseCli{
    Console cnsl = null;

    void GoseCli(){
        // creates a console object
        cnsl = System.console();
    }

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

    void write(String outputText){
        System.out.print(outputText);
    }


}