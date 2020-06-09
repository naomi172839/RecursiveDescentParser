/*
 * Copyright (c) 2020.
 * Author: nbonnin (Naomi Bonnin)
 * Class: CMSC 330 at UMGC
 * Last Modified: 6/8/20, 8:36 PM
 * Description: This program attempts to create a GUI from a text file that contains
 *  commands in the proper BNF.  The grammer is defined in the project description.
 */

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.nio.file.Paths;

import static java.lang.Thread.sleep;

public class ParserTest {

    /*
     * This window represents the provided calculator layout input file
     * that was provided in the project description
     */
    @Test
    public void testCalculatorLayout() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test1.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests a non nested Grid layout with 4 arguments
     * This window tests Label keyword
     * This window tests Textfield keyword
     */
    @Test
    public void testGrid4LabelTextfield() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test2.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Grid Layout - 2 Argument
     *                      Nested Grid Layout - 2 Argument
     *                      Group keyword
     *                      Radio Buttons
     *                      Nested Panels
     *                      Label keyword
     *                      Button keyword
     */
    @Test
    public void testGrid2Grid2GroupRadioPanelLabelButton() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test3.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Flow Layout
     *                      Button Keyword
     *                      Label Keyword
     *                      Textfield Keyword
     */
    @Test
    public void testFlowButtonLabelTextfield() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test4.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Invalid String
     */
    @Test
    public void testInvalidString() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test5.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Missing Parenthesis
     */
    @Test
    public void testMissingParenthesis() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test6.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Invalid grid layout
     */
    @Test
    public void testInvalidGrid() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test7.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Invalid number format
     */
    @Test
    public void testInvalidNumberFormat() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test8.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Invalid Layout Type
     */
    @Test
    public void testInvalidLayoutType() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test9.txt"));
        Parser.parse();
        sleep(10000);
    }

    /*
     * This window tests:
     *                      Missing End keyword
     */
    @Test
    public void testMissingEnd() throws InterruptedException {
        Parser.loadFile(Paths.get("Resources/test10.txt"));
        Parser.parse();
        sleep(10000);
    }
}
