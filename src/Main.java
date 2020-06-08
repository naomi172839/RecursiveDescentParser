import java.io.File;

public class Main {

    public static void main(String[] args) {
        try {
            Parser.loadFile(new File("Resources/test.txt"));
            Parser.parse();
        } catch (Parser.ParseException e) {
            System.out.println(e.getMessage());
        }
    }

}
