package in.canaris.cloud.utils;

public class CMPUtil {

	  
    public static String parseNumberToString(String input) {
		String numericPart = input.replaceAll("[^\\d]", ""); // Remove all non-digit characters
		return numericPart;
	}
}
