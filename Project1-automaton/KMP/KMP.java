package KMP;

import java.util.*;
import java.lang.*;
import java.io.*;

public class KMP {

    private final List<Integer> LTS = new ArrayList<>();
    private final List<Integer> carry_over = new ArrayList<>();
    private final String pattern;

    public KMP(String pattern) {
        this.pattern = pattern;
    }

    public List<Integer> getLTS() {
        return LTS;
    }

    public List<Integer> getCarryOver() {
        return carry_over;
    }

    public String getPattern() {
        return pattern;
    }

    public void display() {
        String sb = "Pattern : " +
                pattern +
                "\n" +
                "LTS : " +
                LTS +
                "\n" +
                "Carry Over : " +
                carry_over;
        System.out.println(sb);
    }

    public void calculateLTS() {
        LTS.clear();
        LTS.add(-1);
        LTS.add(0);

        int length_prefix_suffix = 0;
        int i = 1;
        while (i < pattern.length()) {
            // if prefix-suffix found, increment prefix-suffix's length
            if (pattern.charAt(i) == pattern.charAt(length_prefix_suffix)) {
                length_prefix_suffix++;
                LTS.add(i + 1, length_prefix_suffix);
                i++;
            } else {
                // several consecutive prefix-suffix found
                if (length_prefix_suffix != 0) {
                    length_prefix_suffix = LTS.get(length_prefix_suffix);
                } else {
                    // no prefix-suffix found
                    LTS.add(i + 1, 0);
                    i++;
                }
            }
        }
    }

    public void calculateCarryOver() {
        calculateLTS();
        carry_over.clear();
        carry_over.add(-1);
        for (int i = 1; i < pattern.length(); i++) {
            if (pattern.charAt(i) == pattern.charAt(LTS.get(i))) {
                carry_over.add(LTS.get(LTS.get(i)));
            } else {
                carry_over.add(LTS.get(i));
            }
        }
    }

    public List<Integer> search(String text) {
        calculateCarryOver();
        List<Integer> results = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < text.length(); i++) {
            // no correspondence
            while ((j > 0) && (pattern.charAt(j) != text.charAt(i))) {
                j = carry_over.get(j - 1) + 1;
            }
            // correspondence found
            if (pattern.charAt(j) == text.charAt(i)) {
                j = j + 1;
            }
            // pattern found
            if (j == pattern.length()) {
                results.add(((i + 1) - pattern.length()));
                j = carry_over.get(j - 1) + 1;
            }
        }
        return results;
    }

    // extract content from filepath to string format
    static StringBuilder extractContent(String path) {
        StringBuilder text = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader reader = new BufferedReader(fileReader);

            String line = reader.readLine();
            while (line != null) {
                text.append(line.toLowerCase());
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Read Failed: " + e.getMessage());
        }
        return text;
    }
}

