import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Parser {

    static Queue<String> split;
    static String current;
    static String lookahead;
    static JFrame frame = new JFrame();
    static Container toAddTo;
    static ButtonGroup buttonsToAddTo;

    public static void loadFile(File input) throws ParseException {
        try {
            String overall = new String(Files.readAllBytes(Paths.get("Resources/test.txt")));
            overall = overall.replace("(", " ( ");
            overall = overall.replace(")", " ) ");
            overall = overall.replace(":", " : ");
            overall = overall.replace(";", " ; ");
            overall = overall.replace(".", " . ");
            overall = overall.replace(",", " , ");
            split = new LinkedBlockingQueue<>(Arrays.asList(overall.split("[\\s]")));
            split.removeAll(Collections.singleton(""));
            next();
        } catch (FileNotFoundException e) {
            throw new ParseException("File not Found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void next() {
        current = split.poll();
        lookahead = split.peek();
        if(current == null) {
            return;
        }
        if(current.equals(" ") || current.equals("")) {
            next();
        }
    }

    private static char atHead(String string) {
        return string.charAt(0);
    }

    private static char atTail(String string) {
        return string.charAt(string.length()-1);
    }

    private static void removeHead() {
        current = current.substring(1);
    }

    private static void removeTail() {
        current = current.substring(0, current.length()-1);
    }

    private static void runList() {
        for(String token : split) {
            System.out.print(current+ " ");
            System.out.println(lookahead);
            next();
        }
    }

    private static void string() throws ParseException {
        if(atHead(current) == '"') {
            if(atTail(current) == '"') {
                current = current.substring(1, current.length()-1);
            } else {
                error("Strings must be surrounded with quotations.");
            }
        } else {
            error("Strings must be surrounded with quotations.");
        }
    }

    private static void number() throws ParseException {
        try {
            new BigInteger(current);
        } catch(NumberFormatException e) {
            error(current + " is not a number");
        }
    }

    private static void leftParen() throws ParseException {
        if(current.equals("(")) {
            next();
        } else {
            error("Symbol must be a (, not a " + current);
        }
    }

    private static void rightParen() throws ParseException {
        if(current.equals(")")) {
            next();
        } else {
            error("Symbol must be a ), not a " + current);
        }
    }

    private static void comma() throws ParseException {
        if(current.equals(",")) {
            next();
        } else {
            error("Symbol must be a ,, not a " + current);
        }
    }

    private static void colon() throws ParseException {
        if(current.equals(":")) {
            next();
        } else {
            error("Symbol must be a :, not a " + current);
        }
    }

    private static void semiColon() throws ParseException {
        if(current.equals(";")) {
            next();
        } else {
            error("Symbol must be a ;, not a " + current);
        }
    }

    private static void period() throws ParseException {
        if(current.equals(".")) {
            frame.pack();
            frame.setVisible(true);
        } else {
            error("Symbol must be a ., not a " + current);
        }
    }

    private static void end() throws ParseException {
        if(current.equals("End")) {
            next();
        } else {
            error("Invalid keyword: " + current);
        }
    }

    private static void gui() throws ParseException {
        window();
        layout();
        widgets();
        end();
        period();
    }

    private static void window() throws ParseException {
        if(current.equals("Window")) {
            next();
            string();
            frame = new JFrame(current);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            next();
            leftParen();
            number();
            int dimension1 = Integer.parseInt(current);
            next();
            comma();
            number();
            int dimension2 = Integer.parseInt(current);
            frame.setPreferredSize(new Dimension(dimension1, dimension2));
            frame.setSize(new Dimension(dimension1, dimension2));
            next();
            rightParen();
            toAddTo = frame;
        } else {
            error("Keyword 'Window' is needed");
        }
    }

    private static void layout() throws ParseException {
        if(current.equals("Layout")) {
            next();
            layoutType();
            colon();
        } else {
            error("Keyword 'Layout' is needed");
        }
    }

    private static void widgets() throws ParseException {
        if(!current.equals("End")) {
            widget();
            widgets();
        }
    }

    private static void widget() throws ParseException {
        switch (current) {
            case "Button" -> button();
            case "Group" -> group();
            case "Label" -> label();
            case "Panel" -> panel();
            case "Textfield" -> textfield();
            default -> error("Invalid Keyword: " + current);
        }
    }

    private static void textfield() throws ParseException {
        if(current.equals("Textfield")) {
            next();
            number();
            int dimension1 = Integer.parseInt(current);
            next();
            semiColon();
            TextField field = new TextField();
            field.setColumns(dimension1);
            toAddTo.add(field);
        }
    }

    private static void panel() throws ParseException {
        if(current.equals("Panel")) {
            JPanel panel = new JPanel();
            toAddTo.add(panel);
            toAddTo = panel;
            next();
            layout();
            widgets();
            end();
            semiColon();
            toAddTo = toAddTo.getParent();
        }
    }

    private static void label() throws ParseException {
        if(current.equals("Label")) {
            next();
            string();
            JLabel label = new JLabel(current);
            toAddTo.add(label);
            next();
            semiColon();
        }
    }

    private static void group() throws ParseException {
        if(current.equals("Group")) {
            buttonsToAddTo = new ButtonGroup();
            next();
            radioButtons();
            end();
            semiColon();
        }
    }

    private static void radioButtons() throws ParseException {
        if(!current.equals("End")) {
            radioButton();
            radioButtons();
        }
    }

    private static void radioButton() throws ParseException {
        if(current.equals("Radio")) {
            next();
            string();
            JRadioButton button = new JRadioButton();
            button.setText(current);
            next();
            semiColon();
            toAddTo.add(button);
            buttonsToAddTo.add(button);
        }
    }

    private static void button() throws ParseException {
        if(current.equals("Button")) {
            next();
            string();
            JButton button = new JButton();
            button.setText(current);
            next();
            semiColon();
            toAddTo.add(button);
        }
    }

    private static void layoutType() throws ParseException {
        if(current.equals("Flow")) {
            toAddTo.setLayout(new FlowLayout());
            next();
        } else if(current.equals("Grid")) {
            GridLayout grid = new GridLayout();
            next();
            leftParen();
            number();
            int dimension1 = Integer.parseInt(current);
            next();
            comma();
            int dimension2 = Integer.parseInt(current);
            grid.setRows(dimension1);
            grid.setColumns(dimension2);
            if(lookahead.equals(",")) {
                next();
                comma();
                int dimension3 = Integer.parseInt(current);
                next();
                comma();
                int dimension4 = Integer.parseInt(current);
                next();
                grid.setHgap(dimension3);
                grid.setVgap(dimension4);
            }
            rightParen();
            toAddTo.setLayout(grid);
        }
    }

    public static void parse() throws ParseException {
        gui();
    }

    public static class ParseException extends Exception {

        public ParseException(String message) {
            super(message);
        }
    }

    public static void error(String message) throws ParseException {
        throw new ParseException(message);
    }


}