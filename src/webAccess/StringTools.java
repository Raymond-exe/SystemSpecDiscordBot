package webAccess;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class StringTools {

    //removes all symbols that aren't letters, numbers, spaces, or new-line characters
    public static String cleanString(String text) {
        char index;
        for (int i = 0; i < text.length(); i++) {
            index = text.charAt(i);
            if (index != '\n' && !(index >= '0' && index <= '9') && !(index >= 'a' && index <= 'z') && !(index >= 'A' && index <= 'Z') && index != ' ') {
                text = text.substring(0, i) + text.substring(i + 1);
                i--;
            }
        }

        return text;
    }

    public static String fixString(String text) {
        String symbol;

        while (text.contains("&#")) {
            symbol = text.substring(text.indexOf("&#"), text.indexOf(";", text.indexOf("&#")) + 1);

            text = text.substring(0, text.indexOf(symbol)) + (symbol.equals("&#39;") ? "'" : " ") + text.substring(text.indexOf(symbol) + symbol.length());
        }

        return text;
    }

    public static String removeHtmlTags(String html) {
        int openIndexLocation = html.length();
        int closeIndexLocation;

        while (html.contains("<")) {
            for (int i = 0; i < html.length(); i++) {
                if (html.charAt(i) == '<') {
                    openIndexLocation = i;
                } else if (html.charAt(i) == '>') {
                    closeIndexLocation = i;
                    if (openIndexLocation < closeIndexLocation) {
                        html = html.substring(0, openIndexLocation) + html.substring(closeIndexLocation + 1);
                    }
                }
            }
        }

        return html;
    }

    public static String format(String inputStr) //Crude but somewhat effective means of spacing out HTML
    {
        boolean makeNewLine = false;

        for (int i = 0; i < inputStr.length() - 1; i++) {
            if (inputStr.substring(i, i + 2).equals("</"))
                makeNewLine = true;
            if (inputStr.charAt(i) == '>' && makeNewLine) {
                inputStr = inputStr.substring(0, i + 1) + "\n" + inputStr.substring(i + 1);
                makeNewLine = false;
            }
        }

        return inputStr;
    }

    /*
    public static String buildString(Object[] array){
        String output = "";

        for (int i = 0; i < array.length; i++) {
            output += array[i] + "\n";
        }

        return output;
    } //*/

    /*
    public static ArrayList<String> toStringArrayList(ArrayList<Object> objArray) {
        ArrayList<String> output = new ArrayList<String>();

        for (Object obj : objArray)
            output.add((String) obj);

        return output;
    } //*/

    public static String[] toStringArray(Object[] objArray) {
        String[] output = new String[objArray.length];

        for (int i = 0; i < output.length; i++)
            output[i] = (String) objArray[i];

        return output;
    }

    public static void main(String[] args) {
        System.out.println(cleanString("Hello World!*#($&"));
    }

    public static String getErrorAsStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        return sw.toString();
    } //some more mystic voodoo shit I found online

}
