//imports
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.*;

public class pdfReader {

    private static final int[][] FIXATION_POINTS = {
            {0, 4, 12, 17, 24, 29, 35, 42, 48},
            {1, 2, 7, 10, 13, 14, 19, 22, 25, 28, 31, 34, 37, 40, 43, 46, 49},
            {1, 2, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43, 45, 47, 49},
            {0, 2, 4, 5, 6, 8, 9, 11, 14, 15, 17, 18, 20, 0, 21, 23, 24, 26, 27, 29, 30, 32, 33, 35, 36, 38, 39, 41, 42, 44, 45, 47, 48},
            {0, 2, 3, 5, 6, 7, 8, 10, 11, 12, 14, 15, 17, 19, 20, 21, 23, 24, 25, 26, 28, 29, 30, 32, 33, 34, 35, 37, 38, 39, 41, 42, 43, 44, 46, 47, 48}
    };

    // Method to get the fixation length of a word
    public static int getWordFixationLength(String word, int fixationSize) {
        int wordSize = word.length();
        int[] points = FIXATION_POINTS[fixationSize];

        for (int i = 0; i < points.length; i++) {
            if (wordSize <= points[i]) {
                return i;
            }
        }

        return -1; // Returns an appropriate value when no match is found
    }

    public static String bionicConvert(String str, int fixationLength, String[] sep) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\p{L}(\\p{L}|\\p{Nd})*", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(str);
        StringBuilder result = new StringBuilder();
        int prev = 0;

        while (matcher.find()) {
            int start = matcher.start();
            int end = start + matcher.group().length() - getWordFixationLength(matcher.group(), fixationLength - 1);
            matches.add(str.substring(prev, start) + ((start != end) ? (sep[0] + str.substring(start, end) + sep[1]) : ""));
            prev = end;
        }

        for (String match : matches) {
            result.append(match);
        }

        result.append(str.substring(prev));
        return result.toString();
    }

    //entry and end point of conversion to bionic
    public static void bionicCreator(String input) throws IOException {
        String[] sep = {"**", "**"};
        int fixationLength = 3;
        String converted = bionicConvert(input, fixationLength, sep);
        Process processHTMLFile = Runtime.getRuntime().exec("touch output.md");
        try {
            FileWriter writer = new FileWriter("output.md");
            writer.write(converted);
            writer.close();
            System.out.println("Successfully wrote to file");
        } catch (IOException e) {
            System.out.println("Error");
            e.printStackTrace();
        }
        printResults(processHTMLFile);
        Process processOutput = Runtime.getRuntime().exec("pandoc -s -o output.pdf output.md");

    }

    // printing out any potential results from executing the shell commands
    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    public static void callPython() throws IOException, InterruptedException {
        // change to proper path
        Process callScript = Runtime.getRuntime().exec("python3 summarizer.py");
        System.out.println("summarizer has been called. ");
        TimeUnit.SECONDS.sleep(10);
        Process convertTXTPDF = Runtime.getRuntime().exec("pandoc output.txt -o output.pdf");

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        boolean exit = false;

//        do {
//            System.out.println("Welcome to the ");
//        } while(!exit);

        //Requesting for the name of the file that they have entered
        System.out.println("Enter name of file that's loaded into an input directory in the current directory: ");
        Scanner sc = new Scanner(System.in);
        String fileName = sc.next();

        //Specifying the path of the file
        String filePath;
        filePath = System.getProperty("user.dir") +"/output.pdf";
        callPython();

        try {
            //Specifying the path of the file
            // Loading the PDF document

            TimeUnit.SECONDS.sleep(10);
            File file = new File("output.pdf");
            PDDocument document = Loader.loadPDF(file);

            //Instantiating the text stripper
            PDFTextStripper textStrip = new PDFTextStripper();

            //Stripping the document
            String pdfText = textStrip.getText(document);
            bionicCreator(pdfText);
            document.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

}
