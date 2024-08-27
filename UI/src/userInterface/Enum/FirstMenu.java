package userInterface.Enum;

public enum FirstMenu{
    CREATE_SHEET("Create Sheet"),
    XML_FILE("Load sheet (XML)"),
    BINARY_FILE("Load sheet (BINARY)"),
    EXIT("Exit the system");

    private final String description;

    FirstMenu(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static void PrintMenu() {
        System.out.println("=== " + "First Menu" + " ===");
        int number = 1;
        for (MainMenu option : MainMenu.values()) {
            System.out.printf("%d. %s%n", number++, option.getDescription());
        }
        System.out.println("=================");
    }

}
