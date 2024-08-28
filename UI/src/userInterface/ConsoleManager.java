package userInterface;
import shticell.sheet.coordinate.CoordinateFactory;
import userInterface.Enum.FirstMenu;
import userInterface.Enum.MainMenu;
import controller.Controller;
import dto.SheetDto;

import java.text.NumberFormat;
import java.util.*;


public class ConsoleManager {

    private  Controller manager = new Controller();//important new!!!
    private final Scanner scanner = new Scanner(System.in);
    private final int VERSION = 1;
    private final int NUM_OF_COLS = 26;
    private final int NUM_OF_ROWS = 26;
    private final int ROWS_HEIGHT = 1;
    private final int COLS_WIDTH = 5;

    ConsoleManager(){}

    private void printColsLetters(SheetDto sheetDto) {
        String space = " ";
        int maxRowNumber = sheetDto.numberOfRows();
        int rowNumberWidth = String.valueOf(maxRowNumber).length() + 2;
        System.out.println("Sheet name: " + sheetDto.Name());
        System.out.println("Version: " + sheetDto.version());
        System.out.print(space.repeat(rowNumberWidth) + "|");

        for (int col = 0; col < sheetDto.numberOfColumns(); col++) {
            int colWidth = sheetDto.colsWidth();
            colWidth = colWidth % 2 == 0 ? colWidth + 1 : colWidth;

            int leftPadding = colWidth / 2;
            int rightPadding = colWidth - leftPadding - 1;

            System.out.print(space.repeat(leftPadding));
            System.out.print((char) (col + 'A'));
            System.out.print(space.repeat(rightPadding));
            System.out.print("|");
        }

        System.out.println();
    }


    private void printSheetBody(SheetDto sheetDto) {
        String space = " ";
        int rowHeight = sheetDto.rowsHeight();
        int maxRowNumber = sheetDto.numberOfRows();
        int rowNumberWidth = String.valueOf(maxRowNumber).length() + 2;
        int colWidth = sheetDto.colsWidth() % 2 == 0 ? sheetDto.colsWidth() + 1 : sheetDto.colsWidth();

        for (int row = 1; row <= sheetDto.numberOfRows(); row++) {
            for (int h = 0; h < rowHeight; h++) {  // Repeat for the row height
                if (h == rowHeight / 2) {
                    System.out.print(String.format("%" + rowNumberWidth + "d", row));
                } else {
                    System.out.print(space.repeat(rowNumberWidth));
                }
                System.out.print("|");
                for (int col = 0; col < sheetDto.numberOfColumns(); col++) {
                    String cellValue = addThousandsSeparator(
                            sheetDto.cells().get(
                                            CoordinateFactory.getCoordinate(row, col))
                                    .effectiveValue().value().toString());

                    if (h == rowHeight / 2) {
                        System.out.print(cellValue);
                        int count = colWidth - cellValue.length();
                        System.out.print(space.repeat(Math.abs(count)));
                    } else {
                        System.out.print(space.repeat(colWidth));
                    }
                    System.out.print("|");
                }
                System.out.println();
            }
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

    private void sheetFromXMLFile(){
        System.out.println("Enter File Path");
        String fileDirectory = scanner.nextLine();
        try {
            manager.GetSheetFromXML(fileDirectory);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
//
private void createMangerFromFile() throws Exception{
    System.out.println("Enter File Path");
    String fileDirectory = scanner.nextLine();
    manager = new Controller(fileDirectory);

}

    private void importSheetToBinaryFile()  {
        System.out.println("Enter Full Path name of the sheet file");
        String path = scanner.nextLine();
        try{
            manager.InsertSheetToBinaryFile(path);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void getSheetFromBinaryFile(){
        System.out.println("Enter Full Path name of the sheet file");
        String path = scanner.nextLine();
            try {
                manager.GetSheetFromBinaryFile(path);
            }
            catch (Exception  e) {
               System.out.println(e.getMessage());
            }
    }

     private void createManger(){
        System.out.println("Enter Sheet Name");
        String sheetName = scanner.nextLine();
        manager = new Controller(new SheetDto(sheetName,VERSION,NUM_OF_ROWS,NUM_OF_COLS,COLS_WIDTH,ROWS_HEIGHT));
     }

     void FirstMenuRun() {
        Scanner scanner = new Scanner(System.in);
        boolean validSelection = false;
        while (!validSelection) {
            try {
                FirstMenu.PrintMenu();
                System.out.print("Please select an option (1-4): ");
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1":
                        createManger();
                        validSelection = true;
                        break;
                    case "2":
                        createMangerFromFile();
                        validSelection = true;
                        break;
                    case "3":
                        getSheetFromBinaryFile();
                        validSelection = true;
                        break;
                    case "4":
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid selection. Please choose a number between 1 and 4.");
                        break;
                }
            }
            catch (Exception e) {
                System.out.println("Invalid Path");
            }
        }
     }




    public void Run() {
        boolean goOn = true;
        FirstMenuRun();
        try {
            do {
                MainMenu.PrintMenu();
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> sheetFromXMLFile();
                    case "2" -> printSheetCell(manager.getSheet());
                    case "3" -> printCellValue();
                    case "4" -> insertData();
                    case "5" -> showVersions();
                    case "6" -> importSheetToBinaryFile();
                    case "7" -> getSheetFromBinaryFile();
                    case "8" -> goOn = false;
                    default -> {
                        System.out.println("Invalid choice. Please choose a number 1-8");
                    }
                }
            } while (goOn);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


}