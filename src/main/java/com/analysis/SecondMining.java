package com.analysis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * is created by aMIN on 5/9/2018 at 23:38
 */
public class SecondMining {
    Scanner scanner;
    FileReader reader = null;
    private String fileName;

    SecondMining(String path, String fileName) {
        this.fileName = fileName;

        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        scanner = new Scanner(reader);
    }

    private void createCSV() throws IOException {
        String total = "";
        int[] exps = new int[]{6, 13, 20, 27, 34, 41, 48, 55, 62, 69, 76};
        int counter = 0;
        while (scanner.hasNextLine()) {
            counter++;
            String line = scanner.nextLine();
            if (line.contains("--"))
                continue;
            else {
                if (counter!=3)
                for (int i = 0; i < exps.length; i++)
                    try {
                        if (line.charAt(exps[i]) == ' ') {
                            StringBuilder stringBuilder = new StringBuilder(line);
                            stringBuilder.setCharAt(exps[i], 'N');
                            line = stringBuilder.toString();
                        }
                    } catch (StringIndexOutOfBoundsException e) {

                        for (int j = i; j < exps.length; j++) {
                            StringBuilder ana = new StringBuilder(line);
                            int analength = ana.length();
                            int desire = exps[j];
                            for (int k = analength; k < desire; k++) {
                                ana.insert(k, " ");
                            }
                            ana.insert(ana.length(), "N");

                            line = ana.toString();
                        }
                        break;
                    }




                line = line.replace(" ", ";");
                String s = line;
                for (; ; )
                    if (s.contains(";;"))
                        s = s.replaceAll(";;", ";");
                    else
                        break;
                if (s.charAt(0) == ';')
                    s = s.replaceFirst(";", "");
                if (s.charAt(s.length() - 1) == ';')
                    s = s.substring(0, s.length() - 1);
                total += s + "\r\n";
            }

        }

        RawMining.writeInFileInOnce(System.getProperty("user.dir") + "/assets", this.fileName + ".csv", new StringBuilder(total), true);


    }


    public static void main(String[] args) {
        try {
            new SecondMining("C:\\Users\\AminAbvaal\\Desktop\\javas\\kasri\\assets\\00Z_08 _Jan _2017", "00Z_08 _Jan _2017").createCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}