package org.kodigo.tasklist.views;

public class ExternalViewMethods {
  private ExternalViewMethods() {}

  public static String cutString(String text) {
    return (text.length() > 24 ? text.substring(0, 22) + "..." : text);
  }

  public static void putTitle(String title) {
    System.out.println(String.valueOf('-').repeat(170));
    System.out.printf("%95s", title + "\n");
    System.out.println(String.valueOf('-').repeat(170));
  }

  public static void displayMultiline(String title, String text) {
    System.out.print(title);
    int startIndex = 0;
    int endIndex = 100;
    int textLength = text.length();

    while (startIndex < textLength) {
      if (endIndex >= textLength) {
        endIndex = textLength;
      } else {
        while (endIndex > startIndex && !Character.isWhitespace(text.charAt(endIndex))) {
          endIndex--;
        }
      }

      String line = text.substring(startIndex, endIndex).trim();
      System.out.println(line);
      startIndex = endIndex;
      endIndex += 100;
    }
  }
}
