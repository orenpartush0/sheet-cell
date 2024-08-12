package userInterface;
import userInterface.Enum.MainMenu;
import controller.Controller;
import dto.SheetDto;

import java.util.List;
import java.util.Scanner;


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
        manager = new Controller(new SheetDto(sheetName,VERSION,NUM_OF_ROWS, NUM_OF_COLS,null));
    }

    private void printColsNumbers(SheetDto sheetDto){
        List<Integer> colsWidths = sheetDto.colWidth;
        String space = " ";
        System.out.println("Sheet name: " + sheetDto.sheetName);
        System.out.println("Version: " + sheetDto.version);
        System.out.print(space.repeat(5) + "|");
        for(int col = 0; col < sheetDto.numberOfColumns; col++) {
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
        List<Integer> colsWidths = sheetDto.colWidth;

        for(int row = 0; row < sheetDto.numberOfRows; row++) {
            System.out.print(space.repeat(2));
            System.out.print((char) ('A' + row));
            System.out.print(space.repeat(2));
            System.out.print("|");
            for (int col = 0; col < sheetDto.numberOfColumns; col++) {
                int colWidth = colsWidths.get(col) < 5 ? 5 : colsWidths.get(col);
                colWidth = colWidth  % 2 == 0 ? colWidth + 1 : colWidth;
                String square = String.valueOf((char) ('A' + row)) + String.valueOf(col + 1);
                System.out.print(sheetDto.cells.get(square).effectiveValue);
                String SpaceColsBiggerThenTen = " ".repeat(col >= 9 ? 1 : 0);
                System.out.print(space.repeat(colWidth - sheetDto.cells.get(square).effectiveValue.length()) + SpaceColsBiggerThenTen);
                System.out.print("|");
            }

            System.out.println();
        }
    }

    private void printSheetCell(){
        SheetDto sheetDto = manager.getSheet();
        printColsNumbers(sheetDto);
        printSheetBody(sheetDto);
    }

    public boolean isValidCellIDFormat(String input) {
        String pattern = "(?i)^[a-z][1-9][0-9]*$";
        if (input.matches(pattern)) {
            int numberPart = Integer.parseInt(input.substring(1));
            return numberPart <= manager.getSheet().numberOfColumns + 1;
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

    public void InsertData(){
        try {
            String cellId = selectCell();
            System.out.println("Enter the desired cell data");
            manager.UpdateCellByIndex(cellId, scanner.nextLine());
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
            InsertData();
        }
    }

    public void Run(){
        String choice;
        do {
            MainMenu.PrintMenu();
            choice = scanner.nextLine();
            switch (choice) {
                case "2" -> printSheetCell();
                case "3" -> printCellValue();
                case "4" -> InsertData();
                default -> System.out.println("Invalid choice. Please choose a number 1-6");
            }
        }while(choice != "5");
    }
}