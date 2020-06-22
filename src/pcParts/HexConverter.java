package pcParts;

import java.awt.*;

public class HexConverter
{
  public static String toHex(int goal) //Int To Hex objective: to take in any range of ints and output a hex
  {
    int outputLen = 0;
    int[] nums;
    char[] output;
    String outputStr = "";
    
    //find length output[] should be; each element is one hex digit, so must be 0-15 
    for (int i = 0; i < goal; i = (int)Math.pow(16, outputLen) - 1)
      outputLen++;
    nums = new int[outputLen];
    output = new char[outputLen];
    
    for (int i = 0; i < outputLen; i++) //for loop that populates nums[], then uses nums[] to populate output[]
    {
      //populating nums[]
      nums[i] = goal % 16;
      goal = goal / 16;
      
      //populating output[]
      if (nums[i] < 10)
        output[i] = (char)(nums[i] + 48);
      else
        output[i] = (char)(nums[i] + 55);
    }
    
    //output[] to outputStr
    for (int i = 0; i < outputLen; i++)
      outputStr = output[i] + outputStr;
    
    return outputStr; //*/
  }
  
  //overloaded toHex, to control length of hex;
  public static String toHex(int goal, int length)
  { //*
    String output = toHex(goal); //gets hex of goal # assigns it to output
    if (length < output.length()) //checks if requested length is shorter than hex
      return "-1";
    while (output.length() < length) //extends output to requested length (without adding value)
      output = "0" + output; //*/
    return output;
  }
  
  public static int toInt(String input) //Hex back to Int
  {
    input = input.toUpperCase();
    int output = 0;
    int[] outArray = new int[input.length()];
    
    for (int i = 0; i < input.length(); i++)
    {
      //assigning value
      switch(input.charAt(i))
      {
        case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
          outArray[i] = (int)(input.charAt(i) - 48); //48 = ascii char '0' (number)
          break;
        case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
          outArray[i] = (int)(input.charAt(i) - 55); //55 = ascii char 'A' (letter)
          break;
      }
    }
    
    //populate output using outArray[]
    for (int i = 0; i < outArray.length; i++)
      output += outArray[i] * Math.pow(16, (input.length()-(i+1)) );
    
    return output; //return statement
  } //Misc note: toInt doesn't need an overloaded method to set length. What are you gonna do, add zero?

  public static Color toColor(String hex) {
    int red = toInt(hex.substring(0, 2));
    int green = toInt(hex.substring(2, 4));
    int blue = toInt(hex.substring(4, 6));

    return new Color(red, green, blue);
  }

  public static String toColorHexVal(Color c) {
    String hex = "";

    hex += toHex(c.getRed());
    hex += toHex(c.getGreen());
    hex += toHex(c.getBlue());

    return hex;
  }

  public static void main(String[] args)
  {
    System.out.println("To Int: " + HexConverter.toInt(args[0]));
    System.out.println("To Hex: " + HexConverter.toHex(Integer.parseInt(args[0]), Integer.parseInt(args[1])));
  }
}