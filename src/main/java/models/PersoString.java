package models;

public class PersoString {

	// Set a char at
	public static String setCharAt(String s, int i, char c) {

		StringBuilder sb = new StringBuilder();
		sb.append(s.substring(0, i));
		sb.append(c);
		if (i < s.length() - 1)
			sb.append(s.substring(i + 1));

		return sb.toString();

	}
}
