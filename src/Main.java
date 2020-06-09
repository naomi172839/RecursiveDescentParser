/*
 * Copyright (c) 2020.
 * Author: nbonnin (Naomi Bonnin)
 * Class: CMSC 330 at UMGC
 * Last Modified: 6/8/20, 6:04 PM
 * Description: This program attempts to create a GUI from a text file that contains
 *  commands in the proper BNF.  The grammar is defined in the project description.
 */

/*
 * Import needed to provide the path to the text file.
 */
import java.nio.file.Paths;

/*
 * The main class represents the entry point into the program.
 * It simply calls the static load and parse methods from Parser.
 */
public class Main {

    /*
     * Standard main method.
     * Entry point to the program.
     * Calls static methods from parse.
     */
    public static void main(String[] args) {
        Parser.loadFile(Paths.get("Resources/test1.txt"));  //Load in the file
        Parser.parse(); //Perform the actual parse
    }

}
