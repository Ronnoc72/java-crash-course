import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Arrays;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    // constants
    private static final String FILE = "data.txt";
    private static final String DATE_FORMAT = "uuuu/MM/dd";

    public static void main(String[] args) {
        welcome();
        Scanner scanner = new Scanner(System.in);
        // main loop
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
            } else if (answer.contentEquals("view")) {
                viewCommand();
            } else {
                System.out.printf("No command %s exists, type 'help' to see the commands.%n", answer);
            }
        }
    }

    public static String getDateData(String filename) {
        // gets the raw string data of the dates, with the filename.
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

    public static String[] getDates(String datesData) {
        // returns an array of dates that is extracted from the raw string date data (getDateData).
        String[] rawDateArray = datesData.split(";");
        String[] dateArray = new String[rawDateArray.length];
        for (int i = 0; i < rawDateArray.length; i++) {
            int index = rawDateArray[i].indexOf(":");
            String sub = rawDateArray[i].substring(index+1, rawDateArray[i].length()-1);
            dateArray[i] = sub;
        }
        return dateArray;
    }

    public static int binarySearch(String[] arr, String t) {
        // uses the binarySearch built in method to return the index of element.
        int index = Arrays.binarySearch(arr, t.toString());
        return (index < 0) ? -1 : index;
    }

    public static boolean verifyDate(String[] arr) {
        // makes sure that the date is in the correct format.
        boolean verifiedDate = false;
        if (arr.length == 3) {
            if ((arr[0].length() == 4 && arr[1].length() == 2 && arr[2].length() == 2) &&
                    (Integer.parseInt(arr[1]) < 13 && Integer.parseInt(arr[2]) < 32)) {
                int results = 0;
                for (String s : arr) {
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
        return verifiedDate;
    }

    public static void removeFromFile(String filename, String removal) {
        // removes a date that the user choices from the file.
        String data = getDateData(filename);
        if (!data.isEmpty()) {
            String[] rawDateArray = data.split(";");
            String[] dateArray = getDates(data);
            int result = binarySearch(dateArray, removal);
            if (result != -1) {
                String[] copy = new String[rawDateArray.length-1];
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
                System.out.printf("Date %s doesn't exist in %s%n", removal, FILE);
            }
        } else {
            System.out.printf("No Dates in %s%n", FILE);
        }

    }

    public static void appendToFile(String filename, String text) {
        // appends new date to the dates file, or adds to a date that already exists.
        LocalDate localDate = LocalDate.now();
        String localDateString = localDate.toString().trim();
        String data = getDateData(FILE);
        int milesTotal = Integer.parseInt(text);
        if (!data.isEmpty()) {
            String[] rawDateArray = data.split(";");
            String[] dateArray = getDates(data);
            int result = binarySearch(dateArray, localDateString);
            if (result != -1) {
                String[] prevMilesArray = rawDateArray[result].split(":");
                String prevMiles = prevMilesArray[0].substring(1, prevMilesArray[0].length());
                milesTotal += Integer.parseInt(prevMiles);
                rawDateArray[result] = String.format("[%s:%s];", Integer.toString(milesTotal), localDateString);
            } else {
                String[] copy = new String[rawDateArray.length+1];
                for (int i = 0; i < rawDateArray.length; i++) {
                    copy[i] = rawDateArray[i];
                }
                rawDateArray = copy;
                rawDateArray[rawDateArray.length-1] = String.format("[%s:%s];", Integer.toString(milesTotal), localDateString);
            }
            try {
                FileWriter myWriter = new FileWriter(filename);
                myWriter.write(String.join(";", rawDateArray));
                myWriter.close();
            } catch (IOException e) {
                System.out.println("A file writing error occurred.");
                e.printStackTrace();
            }
        }
    }

    public static void displayDates(String first, String second) {
        // displays the dates that are in the range of first and second.
        String data = getDateData(FILE);
        if (!data.isEmpty()) {
            String[] rawDateArray = data.split(";");
            String[] dateArray = getDates(data);
            int firstBound = binarySearch(dateArray, first);
            int secondBound = binarySearch(dateArray, second);
            if (firstBound == -1) {
                System.out.printf("%s doesn't exist in %s%n", firstBound, FILE);
            }
            if (secondBound == -1) {
                System.out.printf("%s doesn't exist in %s%n", secondBound, FILE);
            }
            if (firstBound == -1 || secondBound == -1) {
                return;
            }
            for (int i = firstBound; i <= secondBound; i++) {
                int index = rawDateArray[i].indexOf(":");
                String miles = rawDateArray[i].substring(1, index);
                String date = rawDateArray[i].substring(index+1, rawDateArray[i].length()-1);
                System.out.printf("miles: %s <==> date: %s%n", miles, date);
            }
        }
    }

    public static void welcome() {
        // welcomes the user, giving all the intructions.
        System.out.println("Welcome to the Miles Data program.");
        System.out.println("This program is to store the amount \nof miles that you run per day.");
        System.out.println("Type 'help' for the commands to enter.");
    }

    public static void helpCommand() {
        // gives a quick overview of the commands used.
        System.out.println("HELP:");
        System.out.println("Type 'add' to append a day in the data.");
        System.out.println("Type 'remove' to cut a day in data.");
        System.out.println("Type 'view' to see the data from a certain range.");
        System.out.println("Type 'exit' to leave the current window.");
        System.out.println("Type 'quit' to stop the program.");
    }

    public static void addCommand() {
        // getting how many miles the user ran today.
        Scanner scanner = new Scanner(System.in);

        System.out.println("ADD:");
        System.out.print("How many miles did you run today.> ");

        String miles = scanner.nextLine();
        if (miles.contentEquals("exit")) {
            return;
        }

        appendToFile(FILE, miles);
    }

    public static void removeCommand() {
        // getting date from the user and verifies if in correct format.
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        Scanner scanner = new Scanner(System.in);

        System.out.println("REMOVE:");
        System.out.println("Note: all the activities that are on the date entered will be removed.");
        System.out.print("Type a date (YYYY-MM-DD) that you want to remove.> ");

        String userInput = scanner.nextLine();
        if (userInput.contentEquals("exit")) {
            return;
        }
        String[] dateArray = userInput.split("-");
        boolean verifiedDate = verifyDate(dateArray);
        if (verifiedDate) {
            removeFromFile(FILE, userInput);
        } else {
            System.out.println("Invalid Date.");
        }
    }

    public static void viewCommand() {
        // getting the range of dates that the user wants to see,
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        Scanner scanner = new Scanner(System.in);

        System.out.println("VIEW:");
        System.out.print("Type your first date (YYYY-MM-DD)> ");
        String firstDate = scanner.nextLine();
        System.out.print("\nType your second date, bigger than your first date (YYYY-MM-DD)> ");
        String secondDate = scanner.nextLine();
        if (!verifyDate(firstDate.split("-"))) {
            System.out.println("First Date wasn't in correct format.");
            return;
        } else if (!verifyDate(secondDate.split("-"))) {
            System.out.println("Second Date wasn't in correct format.");
            return;
        }
        if (LocalDate.parse(firstDate).isBefore(LocalDate.parse(secondDate))) {
            displayDates(firstDate, secondDate);
        }
    }
}

