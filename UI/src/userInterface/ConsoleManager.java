package userInterface;
import dto.CellDto;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import userInterface.Enum.MainMenu;
import controller.Controller;
import dto.SheetDto;

import java.text.NumberFormat;
import java.util.*;


public class ConsoleManager {

    private  Controller manager;
    private final Scanner scanner = new Scanner(System.in);
    private final int VERSION = 1;
    private final int NUM_OF_COLS = 26;
    private final int NUM_OF_ROWS = 26;

    ConsoleManager(){
        SheetDto sheetDto;
        manager =null;
    }

    private void printColsLetters(SheetDto sheetDto){
        List<Integer> colsWidths = sheetDto.colWidth();
        String space = " ";
        System.out.println("Sheet name: " + sheetDto.Name());
        System.out.println("Version: " + sheetDto.version());
        System.out.print(space.repeat(6) + "|");
        for(int col = 0; col < sheetDto.numberOfColumns(); col++) {
            int colWidth = colsWidths.get(col) < 5 ? 5 : colsWidths.get(col);
            colWidth = colWidth  % 2 == 0 ? colWidth + 1 : colWidth;
            System.out.print(space.repeat(colWidth/2));
            System.out.print((char)(col + 'A'));
            System.out.print(space.repeat(colWidth/2));
            System.out.print("|");
        }

        System.out.println();
    }

    private void printSheetBody(SheetDto sheetDto){
        String space = " ";
        List<Integer> colsWidths = sheetDto.colWidth();

        for(int row = 1; row <= sheetDto.numberOfRows(); row++) {
            System.out.print(space.repeat(2));
            System.out.print((row));
            System.out.print(space.repeat(2));
            System.out.print(row < 10 ? " " : "");
            System.out.print("|");
            for (int col = 0; col < sheetDto.numberOfColumns(); col++) {
                int colWidth = colsWidths.get(col) < 5 ? 5 : colsWidths.get(col);
                colWidth = colWidth  % 2 == 0 ? colWidth + 1 : colWidth;

                System.out.print(addThousandsSeparator(
                        sheetDto.cells().get(
                                CoordinateFactory.getCoordinate(row,col))
                                .effectiveValue().value().toString()));

                int count = colWidth - addThousandsSeparator(
                        sheetDto.cells().get(
                                        CoordinateFactory.getCoordinate(row,col))
                                .effectiveValue().value().toString()).length();
                System.out.print(space.repeat(count));
                System.out.print("|");
            }

            System.out.println();
        }
    }

    private String addThousandsSeparator(String number) throws NumberFormatException {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(number));
        }
        catch (NumberFormatException e) {
            return number;
        }
    }

    private void printSheetCell(SheetDto sheetDto){
        printColsLetters(sheetDto);
        printSheetBody(sheetDto);
    }

    public boolean isValidCoordinate(String input) {
        String pattern = "(?i)^[a-z][1-9][0-9]*$";
        if (input.matches(pattern)) {
            int numberPart = Integer.parseInt(input.substring(1));
            return numberPart <= manager.getSheet().numberOfColumns();
        }

        return false;
    }

    private String selectCell(){
        System.out.println("Enter the desired cell identity");
        String coordinate = scanner.nextLine();
        while(!CoordinateFactory.isValidCoordinate(coordinate)
                || Integer.parseInt(coordinate.substring(1)) > manager.getSheet().numberOfColumns()){

            System.out.println("Cell " + coordinate + " does not exists");
            System.out.println("Enter the desired cell identity");
            coordinate = scanner.nextLine();
        }

        return coordinate;
    }

    private void printCellValue(){
        String cell = selectCell();
        System.out.println(manager.GetCellByCoordinate(cell));
    }

    private void insertData(){
        String cell = selectCell();
        System.out.println("Enter the desired cell data");
        try {
            manager.UpdateCellByIndex(cell, scanner.nextLine());
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
    private void sheetFromFile(){
        System.out.println("Enter File Path");
        String fileDirectory = scanner.nextLine();
        try {
            manager.createSheetFromFile(fileDirectory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createMangerFromFile() throws Exception {
        System.out.println("Enter File Path");
        String fileDirectory = scanner.nextLine();
        manager = new Controller(fileDirectory);

        }

     private void FirstMenu(){
        boolean doIt = true;
        do{
            System.out.println("Press 1 to create a new sheet or press 2 to upload sheet from file");
            String choice = scanner.nextLine();
            switch (choice){
                case "1" -> {
                    createManger();
                    doIt = false;
                }
                case "2" -> {
                    try {
                        createMangerFromFile();
                        doIt = false;
                    }
                    catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        }while (doIt);
     }

     private void createManger(){
        System.out.println("Enter Sheet Name");
        String sheetName = scanner.nextLine();
        Map<Coordinate, CellDto> cells = new HashMap<>();
        List<Integer> colWidths = new ArrayList<>();
        manager = new Controller(new SheetDto(sheetName,VERSION,NUM_OF_ROWS,NUM_OF_COLS,cells,colWidths));
     }

    public void Run() {
        boolean goOn = true;
        FirstMenu();
        try {
            do {
                MainMenu.PrintMenu();
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> sheetFromFile();
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
            } while (goOn);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}