package home.bikxs.jumiascrapper;

public class AnsiColors {
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String	BACKGROUND_BLACK	= "\u001B[40m";
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_YELLOW_LIGHT = "\u001B[33;1m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";
	private static final String ANSI_WHITE_LIGHT = "\u001B[37;1m";
	private static final String ANSI_BROWN = "\u001B[35m";
	private static final String ANSI_GREY_DARK = ANSI_WHITE + BACKGROUND_BLACK; //"\u001B[30;1m"; //
	private static final String ANSI_CYAN_LIGHT ="\u001B[36;1m";// ANSI_CYAN + BACKGROUND_BLACK;
	private static final String ANSI_RED_LIGHT = "\u001B[31;1m";//ANSI_RED + BACKGROUND_BLACK;
	
	public static String none(String message) {
        return(message);
    }
    //info
    public static String black(String message) {
        return(ANSI_BLACK  +message + ANSI_RESET);
    }
    public static String red(String message) {
        return(ANSI_RED  +message + ANSI_RESET);
    }
    public static String green(String message) {
        return(ANSI_GREEN  +message + ANSI_RESET);
    }
    public static String yellow(String message) {
        return(ANSI_YELLOW  +message + ANSI_RESET);
    }
    public static String yellowLight(String message) {
        return(ANSI_YELLOW_LIGHT  +message + ANSI_RESET);
    }
    public static String blue(String message) {
        return(ANSI_BLUE  +message + ANSI_RESET);
    }
    public static String purple(String message) {
        return(ANSI_PURPLE  +message + ANSI_RESET);
    }
    public static String white(String message) {
        return(ANSI_WHITE  +message + ANSI_RESET);
    }
    public static String whiteLight(String message) {
        return(ANSI_WHITE_LIGHT  +message + ANSI_RESET);
    }
    public static String brown(String message) {
        return(ANSI_BROWN  +message + ANSI_RESET);
    }
    public static String grey(String message) {
        return(ANSI_GREY_DARK  +message + ANSI_RESET);
//        return black(message);
    }
    public static String lightRed(String message) {
        return(ANSI_RED_LIGHT  +message + ANSI_RESET);
    }
    public static String lightCyan(String message) {
        return (ANSI_CYAN_LIGHT  +message + ANSI_RESET);
    }
    public static String cyan(String message) {
        return(ANSI_CYAN  +message + ANSI_RESET);
    }

}
