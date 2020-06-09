/*
 * Copyright (c) 2020.
 * Author: nbonnin (Naomi Bonnin)
 * Class: CMSC 330 at UMGC
 * Last Modified: 6/8/20, 6:03 PM
 * Description: This program attempts to create a GUI from a text file that contains
 *  commands in the proper BNF.  The grammer is defined in the project description.
 */

/*
 * Imports used at various points throughout the program.
 * Concurrent lists are used because of swing multithreading.
 */

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * The parser class is the bread and butter of the program.
 * It performs all of the actual parsing.
 * All the methods are static and can be used from a static context.
 * The loadFile method should be called followed by the parse method
 */
public class Parser {

    /*
     * Class variables used throughout the program
     */
    static Queue<String> split; //Contains the pre-parsed text file
    static String current;  //Current token
    static String lookahead;    //Lookahead token
    static JFrame frame = new JFrame(); //Frame that is displayed
    static Container toAddTo;   //Aids with the use of nested panels
    static ButtonGroup buttonsToAddTo;  //Aids with nested groups

    /*
     * Prepares the text file for parsing.
     * Complex method does a significant amount of preprocessing.
     * Will display a error if the file can not be found.
     * Note that spaces are placed around character literals to allow them to be treated as their own tokens
     */
    public static void loadFile(Path path) {
        try {
            String overall = new String(Files.readAllBytes(path));  //Read in the text file into a string
            overall = overall.replace("(", " ( ");  //Place spaces around the parenthesis
            overall = overall.replace(")", " ) ");  //Places spaces around the parenthesis
            overall = overall.replace(":", " : ");  //Places spaces around the colon
            overall = overall.replace(";", " ; ");  //Place spaces around the semicolon
            overall = overall.replace(".", " . ");  //Places spaces around the period
            overall = overall.replace(",", " , ");  //Places spaces around the commas
            overall = overall.replace("\n", ""); //Removes pesky new line characters
            split = new LinkedBlockingQueue<>(Arrays.asList(overall.split("[\\s]")));   //Splits on whitespace
            split.removeAll(Collections.singleton("")); //Removes any empty strings
            split.removeAll(Collections.singleton("\n"));   //Removes any remaining new line characters
            next(); //Loads in the first token
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage() + " Not Found",  //Displays the error message
                    "Parse Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0); //Exits the program
        }
    }

    /*
     * Helper method to load the next token
     * Note that any remaining spaces and empty strings are ignored.
     * Null checks are performed
     */
    private static void next() {
        current = split.poll(); //Load in current from head of the queue
        lookahead = split.peek();   //Peak at the next token at the head of the queue
        if (current == null) {   //Null check to prevent null pointer exception
            return;
        }
        if (current.equals(" ") || current.equals("")) { //To ignore  spaces and empty strings
            next(); //Recursively go through the tokens until an appropriate token is found
        }
    }

    /*
     * Helper method to check the character at the head of the string
     */
    private static char atHead(String string) {
        return string.charAt(0);    //Return character at the head
    }

    /*
     * Helper method to return the character at the tail of the string
     */
    private static char atTail(String string) {
        return string.charAt(string.length() - 1);    //Return the character at the tail
    }

    /*
     * Helper method to remove quotes from strings and to ensure that multi word strings are captured
     */
    private static void string() throws ParseException {
        if (atHead(current) == '"') {    //Makes sure string is in proper format
            current = current.substring(1); //Remove leading quote
            StringBuilder sb = new StringBuilder(); //Used to hold the string
            while (atTail(current) != '"') { //While there is not a closing parenthesis
                if (current == null) {   //If the end of the file is reached and there is not a closing parenthesis
                    error("Mismatched Quotations");
                }
                sb.append(current).append(" "); //Add the current token to the string builder and adds a space
                next(); //Loads the next token
            }
            current = current.substring(0, current.length() - 1); //Removes tailing quotation
            sb.append(current); //Add current token to the string builder;
            current = sb.toString();    //Make current the total string;
        } else {
            error("Strings must be surrounded with quotations.");   //Will ultimately show a dialog with this as an error
        }
    }

    /*
     * Helper method to check if a string can be converted to an integer
     */
    private static void number() throws ParseException {
        try {
            new BigInteger(current);    //Creates a new BigInteger, will throw NumberFormatException if fails
        } catch (NumberFormatException e) {
            error(current + " is not a number");
        }
    }

    /*
     * Helper method to check for a left parenthesis, and then advance the token
     */
    private static void leftParen() throws ParseException {
        if (current.equals("(")) {
            next();
        } else {
            error("Symbol must be a (, not a " + current);
        }
    }

    /*
     * Helper method to check for a right parenthesis, will advance the next token
     */
    private static void rightParen() throws ParseException {
        if (current.equals(")")) {
            next();
        } else {
            error("Symbol must be a ), not a " + current);
        }
    }

    /*
     * Helper method to check for a comma, will advance to the next token
     */
    private static void comma() throws ParseException {
        if (current.equals(",")) {
            next();
        } else {
            error("Symbol must be a ,, not a " + current);
        }
    }

    /*
     * Helper method to check for a colon, will advance the next token
     */
    private static void colon() throws ParseException {
        if (current.equals(":")) {
            next();
        } else {
            error("Symbol must be a :, not a " + current);
        }
    }

    /*
     * Helper method to check for semi colon, will advance to the next token.
     */
    private static void semiColon() throws ParseException {
        if (current.equals(";")) {
            next();
        } else {
            error("Symbol must be a ;, not a " + current);
        }
    }

    /*
     * Helper method to check for period.
     * This should always be the last token, if properly formatted.
     * Will show the window to the user.
     */
    private static void period() throws ParseException {
        if (current.equals(".")) {
            frame.pack();   //Makes sure everything is arrayed properly
            frame.setVisible(true);
        } else {
            error("Symbol must be a ., not a " + current);
        }
    }

    /*
     * Helper method to check for the keyword 'End', will advance to the next token.
     */
    private static void end() throws ParseException {
        if (current.equals("End")) {
            next();
        } else {
            error("Invalid keyword: " + current);
        }
    }

    /*
     * Method for the non terminal GUI.
     * BNF = gui ::= Window STRING '('NUMBER ',' NUMBER ')' layout widgets End '.'
     */
    private static void gui() throws ParseException {
        window();
        layout();
        widgets();
        end();
        period();
    }

    /*
     * Method for the keyword 'Window'.
     * Creates a new JFrame with the specified title and dimensions.
     * Should follow the progression: Window STRING '('NUMBER ',' NUMBER ')'
     */
    private static void window() throws ParseException {
        if (current.equals("Window")) {
            next(); //Should move token to a string
            string();   //Checks validity of string
            frame = new JFrame(current);    //Sets title of frame
            frame.setLocationRelativeTo(null);  //Show in the center
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //Close the program when the window closes
            next(); //Should be a parenthesis
            leftParen();    //Checks if left parenthesis
            number();   //Checks if a number
            int dimension1 = Integer.parseInt(current); //Saves the dimension as an int
            next(); //Should be a comma
            comma();    //Checks if a comma
            number();   //Checks if valid number
            int dimension2 = Integer.parseInt(current); //Saves the dimension as an int
            frame.setPreferredSize(new Dimension(dimension1, dimension2));  //Sets preferred size
            frame.setSize(new Dimension(dimension1, dimension2));   //Sets actual size
            next(); //Should be a right parenthesis
            rightParen();   //Checks if right parenthesis
            toAddTo = frame;    //Sets the current container to add new widgets to.
        } else {
            error("Keyword 'Window' is needed");
        }
    }

    /*
     * Method for the layout non terminal
     * BNF = layout ::= Layout layout_type ':'
     */
    private static void layout() throws ParseException {
        if (current.equals("Layout")) {  //Check to make sure proper keyword
            next(); //Should be a layout_type
            layoutType();   //Pass control to layoutType
            colon();    //Check if a colon is present
        } else { //If keyword is missing
            error("Keyword 'Layout' is needed");
        }
    }

    /*
     * Method for the non terminal widgets.
     * Note that this is a recursive production
     * BNF = widgets ::= widget widgets | widget
     * widgets will always be followed by the 'End' keyword
     */
    private static void widgets() throws ParseException {
        if (!current.equals("End")) {    //Checks if there are more widgets to add
            widget();
            widgets();
        }
    }

    /*
     * Method for the non terminal widget.
     * BNF = widget ::=
     *                  Button STRING ';' |
     *                  Group radio_buttons End ';' |
     *                  Label STRING ';' |
     *                  Panel layout widgets End ';' |
     *                  Textfield NUMBER ';'
     * Switch is used instead of if else for readability
     */
    private static void widget() throws ParseException {
        switch (current) {
            case "Button" -> button();
            case "Group" -> group();
            case "Label" -> label();
            case "Panel" -> panel();
            case "Textfield" -> textfield();
            default -> error("Invalid Keyword: " + current);    //If invalid keyword
        }
    }

    /*
     * Method for the keyword 'Textfield'.
     * Creates and adds a new textfield.
     * Number represents the number of columns in the textfield
     * BNF = Textfield NUMBER ';'
     */
    private static void textfield() throws ParseException {
        if (current.equals("Textfield")) {
            next(); //Advance to next token
            number();   //Check if valid number
            int dimension1 = Integer.parseInt(current); //Convert to integer
            next(); //Advance to the next token
            semiColon();    //Check for semicolon
            TextField field = new TextField();  //Create the textfield
            field.setColumns(dimension1);   //Set the width
            toAddTo.add(field); //Add to the appropriate panel
        }
    }

    /*
     * Method for the keyword 'Panel'.
     * Creates a new JPanel.
     * Ensures that nested items are added to this until the End keyword is reached.
     * BNF = Panel layout widgets End ';'
     */
    private static void panel() throws ParseException {
        if (current.equals("Panel")) {   //Keyword check
            JPanel panel = new JPanel();    //Creates the panel
            toAddTo.add(panel); //Adds it to the parent object
            toAddTo = panel;    //Sets the panel as the container to add to
            next(); //Next token
            layout();   //Pass control to layout
            widgets();  //Pass control to widgets
            end();      //Check for end
            semiColon();    //Check for semicolon
            toAddTo = toAddTo.getParent();  //Revert to parent container to add things to
        }
    }

    /*
     * Method for keyword 'Label'
     * BNF = Label STRING ';'
     */
    private static void label() throws ParseException {
        if (current.equals("Label")) {   //Keyword check
            next(); //Advance to next token
            string();   //Check if valid string
            JLabel label = new JLabel(current); //Create lanel
            toAddTo.add(label); //Add label to the appropriate container
            next(); //Next token
            semiColon();    //Semicolon check
        }
    }

    /*
     * Method for keyword Group
     * BNF = Group radio_buttons End ';'
     */
    private static void group() throws ParseException {
        if (current.equals("Group")) {   //Keyword check
            buttonsToAddTo = new ButtonGroup(); //Create new buttongroup
            next(); //Next token
            radioButtons(); //Send control to radio buttons
            end();  //End check
            semiColon();    //Semicolon check
        }
    }

    /*
     * Method for the non terminal radio_buttons
     * BNF = radio_buttons ::= radio_button radio_buttons | radio_button
     */
    private static void radioButtons() throws ParseException {
        if (!current.equals("End")) {    //All radio buttons will end at the end keyword
            radioButton();  //Move control to radioButton
            radioButtons(); //Recurse back through for additional
        }
    }

    /*
     * Method for non terminal radio_button
     * BNF = radio_button ::= Radio STRING ';'
     */
    private static void radioButton() throws ParseException {
        if (current.equals("Radio")) {   //Keyword check
            next(); //Next token
            string();   //Check for valid string
            JRadioButton button = new JRadioButton();   //Create new button
            button.setText(current);    //Set the buttons text
            next(); //Next token
            semiColon();    //Semicolon check
            toAddTo.add(button);    //Adds button to the appropriate panel
            if (buttonsToAddTo != null) {
                //Note that all radio buttons must belong to a group
                buttonsToAddTo.add(button); //Adds button to group
            }
        }
    }

    /*
     * Method for the keyword 'Button'
     * BNF = Button STRING ';'
     */
    private static void button() throws ParseException {
        if (current.equals("Button")) { //Keyword check
            next(); //Next token
            string();   //String check
            JButton button = new JButton(); //New Button
            button.setText(current);    //Set button text
            next(); //Next token
            semiColon();    //Semicolon check
            toAddTo.add(button);    //Add button to appropriate panel
        }
    }

    /*
     * Method for the non terminal layout_type
     * BNF = layout_type ::= Flow | Grid '(' NUMBER ',' NUMBER [',' NUMBER ',' NUMBER] ')'
     */
    private static void layoutType() throws ParseException {
        if (current.equals("Flow")) {   //Keyword check
            toAddTo.setLayout(new FlowLayout());    //Sets the layout to appropriate container
            next(); //Advance the token
        } else if (current.equals("Grid")) {    //Keyword check
            GridLayout grid = new GridLayout(); //Creates new layout item
            next(); //Next token
            leftParen();    //Left parenthesis check
            number();   //Valid number check
            int dimension1 = Integer.parseInt(current); //Saves first dimension
            next(); //Next token
            comma();    //Comma check
            number();   //Number check
            int dimension2 = Integer.parseInt(current); //Save second dimension
            grid.setRows(dimension1);   //Set rows to first dimension
            grid.setColumns(dimension2);    //Sets columns to second dimension
            if (lookahead.equals(",")) {    //Handles the optional arguments
                next(); //Next token
                comma();    //Comma check
                number(); //Number check
                int dimension3 = Integer.parseInt(current); //Save dimension
                next(); //Next token
                comma(); //Comma check
                number();   //Number check
                int dimension4 = Integer.parseInt(current); //Save dimension 4
                grid.setHgap(dimension3);   //Set Horizontal spacing
                grid.setVgap(dimension4);   //Set Vertical spacing
            }
            next(); //Next token
            rightParen();   //Right parenthesis check
            toAddTo.setLayout(grid); //Sets the layout to the appropriate container
        } else {
            error("Invalid Keyword: " + current);
        }
    }

    /*
     * Parse method is called to begin the parse.
     * Will show error message for any parse errors
     */
    public static void parse() {
        try {
            gui();  //GUI is the entry non terminal
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Parse Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);

        }
    }

    /*
     * Helper method for throwing an error.
     * Makes code slightly more readable
     */
    public static void error(String message) throws ParseException {
        throw new ParseException(message);
    }

    /*
     * Custom exception for any errors with Parsing
     */
    public static class ParseException extends Exception {

        public ParseException(String message) {
            super(message);
        }
    }

}