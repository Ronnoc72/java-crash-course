package program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final String FILE = "data.txt";
    private static final String DATE_FORMAT = "uuuu/MM/dd";

    public static void main(String[] args) {
        welcome();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String answer = scanner.nextLine().toLowerCase();
            if (answer.contentEquals("help")) {
                helpCommand();
            } else if (answer.contentEquals("quit")) {
                break;
            } else if (answer.contentEquals("add")) {
                addCommand();
            } else if (answer.contentEquals("remove")) {
                removeCommand();
            } else {
                System.out.printf("No command %s exists, type 'help' to see the commands.%n", answer);
            }
        }
    }

    public static String getDateData(String filename) {
        try {
            File file = new File(filename);
            Scanner myReader = new Scanner(file);
            String data = "";
            while (myReader.hasNextLine()) {
                String str = myReader.nextLine();
                data = String.format("%s%s", str, data);
            }
            myReader.close();
            return data;
        } catch (IOException e) {
            System.out.println("A file reading error occurred.");
            e.printStackTrace();
            return "";
        }
    }

    public static int binarySearch(String[] arr, int l, int r, String x) {
        LocalDate xDate = LocalDate.parse(x);
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (arr[mid].contains(x)) {
                return mid;
            }
            LocalDate date = LocalDate.parse(arr[mid]);
            if (date.isBefore(xDate)) {
                return binarySearch(arr, l, mid - 1, x);
            }
            return binarySearch(arr, mid + 1, r, x);
        }
        return l;
    }

    public static void removeFromFile(String filename, String removal) {
        String data = getDateData(filename);
        if (!data.isEmpty()) {
            String[] rawDateArray = data.split(";");
            String[] dateArray = new String[rawDateArray.length];
            for (int i = 0; i < rawDateArray.length; i++) {
                int index = rawDateArray[i].indexOf(":");
                String sub = rawDateArray[i].substring(index+1, rawDateArray[i].length()-1);
                dateArray[i] = sub;
            }
            int result = binarySearch(dateArray, 0, dateArray.length-1, removal);
            System.out.println(result);
            String[] copy = new String[rawDateArray.length - 1];
            for (int i = 0, j = 0; i < rawDateArray.length; i++) {
                if (!rawDateArray[i].equals(rawDateArray[result])) {
                    copy[j++] = rawDateArray[i];
                }
            }
            try {
                FileWriter myWriter = new FileWriter(filename);
                myWriter.write(String.join(";", copy));
                myWriter.close();
            } catch (IOException e) {
                System.out.println("A file writing error occurred.");
                e.printStackTrace();
            }

        } else {
            System.out.printf("No Dates in %s%n", FILE);
        }

    }

    public static void appendToFile(String filename, String text) {
        try {
            File file = new File(filename);
            Scanner myReader = new Scanner(file);
            String data = "";
            while (myReader.hasNextLine()) {
                String str = myReader.nextLine();
                data = String.format("%s%s", str, data);
            }
            myReader.close();
            FileWriter myWriter = new FileWriter(filename);
            myWriter.write(String.format("%s%s;", data, text));
            myWriter.close();
        } catch (IOException e) {
            System.out.println("A file writing error occurred.");
            e.printStackTrace();
        }
    }

    public static void welcome() {
        System.out.println("Welcome to the Miles Data program.");
        System.out.println("This program is to store the amount \nof miles that you run per day.");
        System.out.println("Type 'help' for the commands to enter.");
    }

    public static void helpCommand() {
        System.out.println("HELP:");
        System.out.println("Type 'add' to append a day in the data.");
        System.out.println("Type 'remove' to remove a day in data.");
        System.out.println("Type 'exit' to exit the current window.");
        System.out.println("Type 'quit' to stop the program.");
    }

    public static void addCommand() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDate localDate = LocalDate.now();
        Scanner scanner = new Scanner(System.in);

        System.out.println("ADD:");
        System.out.print("How many miles did you run today.> ");

        try {
            File file = new File(FILE);
            Scanner myReader = new Scanner(file);
            String data = "";
            while (myReader.hasNextLine()) {
                String str = myReader.nextLine();
                data = String.format("%s%s", str, data);
            }
            myReader.close();
        } catch (IOException e) {
            System.out.println("A file writing error occurred.");
            e.printStackTrace();
        }

        String miles = scanner.nextLine();
        String info = String.format("[%s:%s]", miles, localDate);

        appendToFile(FILE, info);
    }

    public static void removeCommand() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        Scanner scanner = new Scanner(System.in);

        System.out.println("REMOVE:");
        System.out.println("Note: all the activities that are on the date entered will be removed.");
        System.out.print("Type a date (YYYY-MM-DD) that you want to remove.> ");

        String userInput = scanner.nextLine();
        String[] dateArray = userInput.split("-");
        boolean verifiedDate = false;
        if (dateArray.length == 3) {
            if ((dateArray[0].length() == 4 && dateArray[1].length() == 2 && dateArray[2].length() == 2) &&
                    (Integer.parseInt(dateArray[1]) < 13 && Integer.parseInt(dateArray[2]) < 32)) {
                int results = 0;
                for (String s : dateArray) {
                    for (int j = 0; j < s.length(); j++) {
                        char c = s.charAt(j);
                        if (Character.isDigit(c)) {
                            results++;
                        }
                    }
                }
                if (results == 8) {
                    verifiedDate = true;
                }
            }
        }
        if (verifiedDate) {
            removeFromFile(FILE, userInput);
        } else {
            System.out.println("Invalid Date.");
        }
    }
}

