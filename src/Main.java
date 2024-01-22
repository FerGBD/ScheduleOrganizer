import java.util.HashMap;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Map;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.awt.print.PrinterException;
import java.awt.print.PageFormat;

public class Main {
    public static void main(String[] args) throws PrintException {
        Map<String, Set<String>> occupiedTimes = new HashMap<>();
        List<String[]> table = initializeTable();
        Map<String, String> schedules = initializeSchedules();

        Scanner sc = new Scanner(System.in);
        boolean continueAdding = true;
        while (continueAdding) {
            System.out.println("Enter the subject you want to enroll in:");
            String sub = sc.nextLine();

            System.out.println("What day is it? (Mon, Tue, Wed, Thu, Fri)");
            String day = sc.next().toUpperCase();
            sc.nextLine();

            Set<String> validDays = new TreeSet<>(Arrays.asList("MON", "TUE", "WED", "THU", "FRI"));
            if (!validDays.contains(day)) {
                System.out.println("Invalid day. Please enter a valid day (Mon, Tue, Wed, Thu, Fri).");
                continue;
            }

            String entry;
            do {
                System.out.println("Enter the entry time:");
                entry = sc.nextLine().toUpperCase();
                if (!schedules.containsKey(entry)) {
                    System.out.println("Invalid entry class code. Please enter a valid code.");
                }
            } while (!schedules.containsKey(entry));

            String exit;
            do {
                System.out.println("Enter the exit class code: ");
                exit = sc.nextLine().toUpperCase();
                if (!schedules.containsKey(exit)) {
                    System.out.println("Invalid exit class code. Please enter a valid code.");
                }
            } while (!schedules.containsKey(exit));


            String exitClass = schedules.get(exit);
            String entryClass = schedules.get(entry);

            String entryDay = entry.substring(0, 1);
            if (entryDay.equals(day) && entryClass.equals(schedules.get(entry))) {
                System.out.println("Schedule conflict! Entry time is already occupied on the same day.");
                sc.nextLine();
                continue;
            }


            if (entry.equals(exit)) {
                System.out.println("Schedule conflict! Entry and exit times cannot be the same.");
                sc.nextLine();
                continue;
            }
            if (checkScheduleConflict(day, entry, exit, occupiedTimes)) {
                System.out.println("Schedule conflict! The time slot is already occupied on the same day.");
                continue;
            }

            updateOccupiedTimes(day, entry, exit, occupiedTimes);



            String[] row = table.get(getRowNumber(day));
            int col = getColumnNumber(entry);
            for (int i = col; i < col + getDuration(entry, exit); i++) {
                row[i] = sub;
            }

            System.out.print("Do you want to continue? (yes/no): ");
            String continueOption = sc.nextLine().toLowerCase();

            if (continueOption.equals("no")) {
                System.out.println("Program ended. Goodbye!");
                continueAdding = false;

                System.out.print("Do you want to print the schedule table? (yes/no): ");
                String printOption = sc.nextLine().toLowerCase();

                if (printOption.equals("yes")) {
                    printTable(table);


                    PrinterJob printerJob = PrinterJob.getPrinterJob();


                    InputStream stream = new ByteArrayInputStream(getPrintableTable(table).getBytes(StandardCharsets.UTF_8));
                    Doc doc = new SimpleDoc(stream, DocFlavor.INPUT_STREAM.AUTOSENSE, null);


                    PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();


                    if (printerJob.printDialog(attributes)) {
                        printTableToPrinter(table);
                    }
                }

                break;
            }
        }

        printTable(table);
        sc.close();
    }

    private static String CalcutaleTheInterval(String entryClass, String exitClass) {
        String[] entryParts = entryClass.split(" - ");
        String[] exitParts = exitClass.split(" - ");

        int entryHour = Integer.parseInt(entryParts[0].split(":")[0]);
        int entryMinute = Integer.parseInt(entryParts[0].split(":")[1]);

        int exitHour = Integer.parseInt(exitParts[1].split(":")[0]);
        int exitMinute = Integer.parseInt(exitParts[1].split(":")[1]);

        int totalMinutesEntry = entryHour * 60 + entryMinute;
        int totalMinutesExit = exitHour * 60 + exitMinute;

        int minuteDifference = totalMinutesExit - totalMinutesEntry;

        int hours = minuteDifference / 60;
        int remainingMinutes = minuteDifference % 60;

        return String.format("%02d:%02d", hours, remainingMinutes);
    }

    private static boolean checkScheduleConflict(String day, String entry, String exit, Map<String, Set<String>> occupiedTimes) {
        Set<String> dayOccupiedTimes = occupiedTimes.getOrDefault(day, new HashSet<>());

        for (String time : dayOccupiedTimes) {
            if (isOverlap(entry, exit, time)) {
                System.out.println("Schedule conflict! The selected time slot is already occupied on the same day.");
                return true;
            }
        }

        return false;
    }

    private static void updateOccupiedTimes(String day, String entry, String exit, Map<String, Set<String>> occupiedTimes) {
        Set<String> dayOccupiedTimes = occupiedTimes.getOrDefault(day, new HashSet<>());

        for (int i = Integer.parseInt(entry.substring(1)); i <= Integer.parseInt(exit.substring(1)); i++) {
            dayOccupiedTimes.add(day + i);
        }

        occupiedTimes.put(day, dayOccupiedTimes);
    }

    private static boolean isOverlap(String entry, String exit, String time) {
        try {
            int entryNumber = Integer.parseInt(entry.substring(1));
            int exitNumber = Integer.parseInt(exit.substring(1));
            int timeNumber = Integer.parseInt(time.substring(1));

            return entryNumber <= timeNumber && timeNumber <= exitNumber;
        } catch (NumberFormatException e) {
            System.out.println("Invalid schedule format. Please enter a valid schedule.");

            return false;
        }

    }

    private static int getColumnNumber(String entry) {
        Map<String, Integer> scheduleToColumn = new HashMap<>();
        scheduleToColumn.put("M1", 0);
        scheduleToColumn.put("M2", 1);
        scheduleToColumn.put("M3", 2);
        scheduleToColumn.put("M4", 3);
        scheduleToColumn.put("M5", 4);
        scheduleToColumn.put("M6", 5);
        scheduleToColumn.put("T1", 6);
        scheduleToColumn.put("T2", 7);
        scheduleToColumn.put("T3", 8);
        scheduleToColumn.put("T4", 9);
        scheduleToColumn.put("T5", 10);
        scheduleToColumn.put("T6", 11);
        scheduleToColumn.put("N1", 12);
        scheduleToColumn.put("N2", 13);
        scheduleToColumn.put("N3", 14);
        scheduleToColumn.put("N4", 15);
        scheduleToColumn.put("N5", 16);

        return scheduleToColumn.get(entry);
    }


    private static List<String[]> initializeTable() {
        List<String[]> table = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            table.add(new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});
        }
        return table;
    }

    private static void printTable(List<String[]> table) {
        if (table.size() < 5) {
            System.out.println("Error: Insufficient data in the table.");
            return;
        }

        System.out.println("  | M1 | M2 | M3 | M4 | M5 | M6 | T1 | T2 | T3 | T4 | T5 | T6 | N1 | N2 | N3 | N4 | N5 |");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (int i = 0; i < 5; i++) {
            System.out.print(getDayAbbreviation(i) + " | ");
            System.out.println(String.join(" | ", table.get(i)) + " |");
            System.out.println("--------------------------------------------------------------------------------------------");
        }
    }


    private static void printScheduleTable(Map<String, String> schedules, Map<String, Set<String>> enrolledCourses) {

        System.out.printf("%-5s", "");

        for (String scheduleCode : schedules.keySet()) {
            System.out.printf("%-15s", scheduleCode);
        }

        System.out.println(); // Nova linha após o cabeçalho

        // Imprime as linhas da tabela
        for (String day : Arrays.asList("MON", "TUE", "WED", "THU", "FRI")) {
            System.out.printf("%-5s", day); // Dia da semana

            for (String scheduleCode : schedules.keySet()) {
                int column = getColumnNumber(scheduleCode);
                String cellValue = enrolledCourses.getOrDefault(day, Collections.emptySet()).contains(scheduleCode) ? "X" : "";
                System.out.printf("%-15s", cellValue);
            }

            System.out.println(); // Nova linha após cada dia
        }
    }
    private static String getDayAbbreviation(int dayIndex) {
        switch (dayIndex) {
            case 0:
                return "MON";
            case 1:
                return "TUE";
            case 2:
                return "WED";
            case 3:
                return "THU";
            case 4:
                return "FRI";
            default:
                return "";
        }
    }

    private static int getDuration(String entry, String exit) {
        String[] entryParts = entry.substring(1).split("");
        String[] exitParts = exit.substring(1).split("");

        int entryNumber = Integer.parseInt(entryParts[0]);
        int exitNumber = Integer.parseInt(exitParts[0]);

        return exitNumber - entryNumber + 1;
    }
    private static int getRowNumber(String day) {
        Map<String, Integer> dayToRow = new HashMap<>();
        dayToRow.put("MON", 0);
        dayToRow.put("TUE", 1);
        dayToRow.put("WED", 2);
        dayToRow.put("THU", 3);
        dayToRow.put("FRI", 4);

        return dayToRow.get(day);
    }
    private static String getDayAbbreviation(String day) {
        switch (day.toUpperCase()) {
            case "MON", "MONDAY" -> {
                return "MON";
            }
            case "TUE", "TUESDAY" -> {
                return "TUE";
            }
            case "WED", "WEDNESDAY" -> {
                return "WED";
            }
            case "THU", "THURSDAY" -> {
                return "THU";
            }
            case "FRI", "FRIDAY" -> {
                return "FRI";
            }
            default -> {
                return "";
            }
        }
    }

    private static String getPrintableTable(List<String[]> table) {
        StringBuilder result = new StringBuilder();

        result.append("  | M1 | M2 | M3 | M4 | M5 | M6 | T1 | T2 | T3 | T4 | T5 | T6 | N1 | N2 | N3 | N4 | N5 |\n");
        result.append("--------------------------------------------------------------------------------------------\n");

        for (int i = 0; i < 5; i++) {
            result.append(getDayAbbreviation(i)).append(" | ");
            result.append(String.join(" | ", table.get(i))).append(" |\n");
            result.append("--------------------------------------------------------------------------------------------\n");
        }

        return result.toString();
    }
    private static void printTableToPrinter(List<String[]> table) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.pageDialog(new PageFormat());
        printerJob.setPrintable(new TablePrintable(table), pageFormat);

        if (printerJob.printDialog()) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
    private static Map<String, String> initializeSchedules() {
        Map<String, String> schedules = new HashMap<>();
        schedules.put("M1", "07:00 - 07:50");
        schedules.put("M2", "07:50 - 08:40");
        schedules.put("M3", "08:50 - 09:40");
        schedules.put("M4", "09:40 - 10:30");
        schedules.put("M5", "10:40 - 11:30");
        schedules.put("M6", "11:30 - 12:20");
        schedules.put("T1", "12:30 - 13:20");
        schedules.put("T2", "13:20 - 14:10");
        schedules.put("T3", "14:20 - 15:10");
        schedules.put("T4", "15:10 - 16:00");
        schedules.put("T5", "16:10 - 17:00");
        schedules.put("T6", "17:00 - 17:50");
        schedules.put("N1", "18:00 - 18:50");
        schedules.put("N2", "18:50 - 19:40");
        schedules.put("N3", "19:40 - 20:30");
        schedules.put("N4", "20:30 - 21:20");
        schedules.put("N5", "21:20 - 22:10");
        return schedules;
    }

}