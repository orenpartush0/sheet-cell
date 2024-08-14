package userInterface;
import userInterface.Enum.MainMenu;
import controller.Controller;
import dto.SheetDto;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class ConsoleManager {

    private final Controller manager;
    private final Scanner scanner = new Scanner(System.in);
    private final int VERSION = 1;
    private final int NUM_OF_COLS = 26;
    private final int NUM_OF_ROWS = 26;

    ConsoleManager(){
        SheetDto sheetDto;
        System.out.println("Please enter the name of the Sheet: ");
        String sheetName = scanner.nextLine();
        manager = new Controller(new SheetDto(sheetName,VERSION,NUM_OF_ROWS, NUM_OF_COLS));
    }

    private void printColsNumbers(SheetDto sheetDto){
        List<Integer> colsWidths = sheetDto.colWidth();
        String space = " ";
        System.out.println("Sheet name: " + sheetDto.Name());
        System.out.println("Version: " + sheetDto.version());
        System.out.print(space.repeat(5) + "|");
        for(int col = 0; col < sheetDto.numberOfColumns(); col++) {
            int colWidth = colsWidths.get(col) < 5 ? 5 : colsWidths.get(col);
            colWidth = colWidth  % 2 == 0 ? colWidth + 1 : colWidth;
            System.out.print(space.repeat(colWidth/2));
            System.out.print(col + 1);
            System.out.print(space.repeat(colWidth/2));
            System.out.print("|");
        }

        System.out.println();
    }

    private void printSheetBody(SheetDto sheetDto){
        String space = " ";
        List<Integer> colsWidths = sheetDto.colWidth();

        for(int row = 0; row < sheetDto.numberOfRows(); row++) {
            System.out.print(space.repeat(2));
            System.out.print((char) ('A' + row));
            System.out.print(space.repeat(2));
            System.out.print("|");
            for (int col = 0; col < sheetDto.numberOfColumns(); col++) {
                int colWidth = colsWidths.get(col) < 5 ? 5 : colsWidths.get(col);
                colWidth = colWidth  % 2 == 0 ? colWidth + 1 : colWidth;
                String square = String.valueOf((char) ('A' + row)) + String.valueOf(col + 1);
                System.out.print(sheetDto.cells().get(square).effectiveValue());
                String SpaceColsBiggerThenTen = " ".repeat(col >= 9 ? 1 : 0);
                System.out.print(space.repeat(colWidth - sheetDto.cells().get(square).effectiveValue().length()) + SpaceColsBiggerThenTen);
                System.out.print("|");
            }

            System.out.println();
        }
    }

    private void printSheetCell(SheetDto sheetDto){
        printColsNumbers(sheetDto);
        printSheetBody(sheetDto);
    }

    public boolean isValidCellIDFormat(String input) {
        String pattern = "(?i)^[a-z][1-9][0-9]*$";
        if (input.matches(pattern)) {
            int numberPart = Integer.parseInt(input.substring(1));
            return numberPart <= manager.getSheet().numberOfColumns();
        }

        return false;
    }

    private String selectCell(){
        System.out.println("Enter the desired cell identity");
        String cellId = scanner.nextLine().toUpperCase();
        while(!isValidCellIDFormat(cellId)){
            System.out.println("Cell " + cellId + " does not exists");
            System.out.println("Enter the desired cell identity");
            cellId = scanner.nextLine();
        }

        return cellId;
    }

    private void printCellValue(){
        String cellId = selectCell();
        System.out.println(manager.GetCellDataByID(cellId));
    }

    private void insertData(){
        String cellId = selectCell();
        System.out.println("Enter the desired cell data");
        try {
            manager.UpdateCellByIndex(cellId, scanner.nextLine());
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void showCountOfChangesPerVersions(List<Integer> countsOfChangesPerVersion) {
        System.out.println("Count of Changes Per Version:");
        for (int i = 0; i < countsOfChangesPerVersion.size(); i++) {
            System.out.printf("Version %d: %d changes%n", i + 1, countsOfChangesPerVersion.get(i));
        }

        System.out.println();

    }
    private static boolean isInteger(String str) {
        return str.matches("-?\\d+");
    }

    private int showSheetVersionByUserChoice(List<Integer> countsOfChangesPerVersion){
        String versionNumber;

        while (true) {
            System.out.print("Enter the version number (1 to " + countsOfChangesPerVersion.size() + "): ");
                versionNumber = scanner.nextLine();
                if(isInteger(versionNumber)){
                    if (Integer.parseInt(versionNumber) >= 1 && Integer.parseInt(versionNumber) <= countsOfChangesPerVersion.size()) {
                        break;
                    }
                }

            System.out.println("Invalid input. Please enter a number between 1 and " + countsOfChangesPerVersion.size() + ".");
        }

        return Integer.parseInt(versionNumber);
    }

    private void showVersions(){
        List<Integer> countsOfChangesPerVersion = manager.GetCountOfChangesPerVersion();
        showCountOfChangesPerVersions(countsOfChangesPerVersion);
        int selectedVersion = showSheetVersionByUserChoice(countsOfChangesPerVersion);
        printSheetCell(manager.GetSheetByVersion(selectedVersion));
    }

    public void Run(){
        boolean goOn = true;

        do {
            MainMenu.PrintMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "2" -> printSheetCell(manager.getSheet());
                case "3" -> printCellValue();
                case "4" -> insertData();
                case "5" -> showVersions();
                default -> {
                    if (choice.equals("6")) {
                        System.out.println("Bye Bye");
                    } else {
                        System.out.println("Invalid choice. Please choose a number 1-6");
                    }
                }
            }
            goOn = !choice.equals("6");
        }while(goOn);
    }


}