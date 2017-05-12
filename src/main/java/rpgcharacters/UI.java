package rpgcharacters;

import java.lang.NumberFormatException;
import java.util.List;
import java.util.Scanner;

public class UI {

    public static int ERROR_INT = java.lang.Integer.MIN_VALUE;

    private static String ANSI_CLS = "\u001b[2J";
    private static String ANSI_HOME = "\u001b[H";

    private static String div1 = "============================================================";
    private static String div2 = "------------------------------------------------------------";

    private UI( Scanner sc ) {
    }

    public static int promptInt( Scanner sc, String prompt, int min, int max ) {
        return promptInt( sc, prompt, min, max, -1 );
    }

    public static int promptInt( Scanner sc, String prompt, int min, int max, int max_attempts ) {

        int input = ERROR_INT;
        int attempt = 0;
        boolean valid_input = false;
        boolean attempts_ok = true;
        boolean input_in_range = false;

        String invalid_output = "Invalid input";

        do {

            attempt++;

            try {
                printOutput( prompt, false );
                input = Integer.parseInt( sc.nextLine() );
            } catch ( NumberFormatException e ) {
                input = ERROR_INT;
            }

            // input is within specified range AND input is not default
            valid_input = ( input >= min && input <= max && input != ERROR_INT );
            // attempts are unlimited OR attempt is under max allowed
            attempts_ok = ( max_attempts < 1 || attempt < max_attempts );

            // if the input was invalid, say so
            if ( !valid_input ) {
                printOutput( invalid_output, false );

                // if there are a limited number of attempts, say how many are left
                if ( max_attempts > 0 ) {
                    System.out.print( " (Attempt " + attempt + "/" + max_attempts + ")" );
                }

                System.out.println( "..." );
            }

        // loop if the input was invalid AND there are remaining attempts
        } while ( !valid_input && attempts_ok );

        if ( valid_input ) {
            return input;
        }

        if ( !attempts_ok ) {
            printOutput( "Too many attempts!" );
        }

        return ERROR_INT;

    }

    public static void printMenuTitle( String title ) {

        System.out.println();
        System.out.println( div1 );
        System.out.println( "  " + title );

    }

    public static void printOptions( List<String> options ) {
        printOptions( options, "Available options:" );
    }

    public static void printOptions( List<String> options, String title ) {

        printDiv2();

        String optionsString = "  " + title;
        String optionFormat  = "\n\t%d: %s";

        for ( int i = 0; i < options.size(); i++ ) {
            optionsString += String.format( optionFormat, (i+1), options.get( i ) );
        }

        System.out.println( optionsString );

        printDiv2();

    }

    public static void printOutput( String output ) {
        printOutput( output, true );
    }

    public static void printOutput( String output, boolean newline ) {
        System.out.print( "  > " + output );

        if ( newline ) {
            System.out.println();
        }
    }

    public static void printParagraph( String p, int max_len ) {
        printParagraph( p, max_len, 2 );
    }

    public static void printParagraph( String p, int max_len, int indent_len ) {
        System.out.println( formatParagraph( p, max_len, indent_len ) );
    }

    public static String formatParagraph( String p, int max_len, int indent_len ) {

        String[] pTokens = p.split(" ");

        String indent = "";
        for ( int i = 0; i < indent_len; i++ ) {
            indent += " ";
        }

        int curLen = indent_len;

        String output = "";

        output += indent;

        for ( String tok : pTokens ) {
            if ( curLen + tok.length() > max_len ) {
                output += "\n" + indent + tok;
                curLen = indent.length() + tok.length();
            }
            else {
                output += tok + " ";
                curLen += tok.length() + 1;
            }
        }

        return output;

    }

    public static void printStats( int[] mods ) {

        System.out.println( "  POWER : PROFICIENCY : PERSONALITY : PERCEPTION" );
        String modFormat =  "   %3d  :     %3d     :     %3d     :    %3d    ";
        System.out.println( String.format( modFormat,
                              mods[0],   mods[1],      mods[2],     mods[3] )
        );

    }

    public static void printDiv1() { System.out.println( div1 ); }
    public static void printDiv2() { System.out.println( div2 ); }

    public static void clearScreen() {

        System.out.print( ANSI_CLS + ANSI_HOME );
        System.out.flush();

    }

}
