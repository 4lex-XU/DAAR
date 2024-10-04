package KMP;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] arg) {
        String path;
        String pattern;
        if (arg.length > 1) {
            path = arg[0];
            pattern = arg[1].toLowerCase();
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.println(" >> Please enter the file's path: ");
            path = scanner.next();
            System.out.println(" >> Please enter the pattern: ");
            pattern = scanner.next().toLowerCase();
        }

        StringBuilder text = KMP.extractContent(path);

        KMP kmp = new KMP(pattern);
        List<Integer> results = kmp.search(text.toString());
        kmp.display();
        System.out.println("Search CarryOver Results: " + results);
    }
}
