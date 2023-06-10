package views;

public class ExternalViewMethods {
  private ExternalViewMethods() {}

  public static String cutString(String text) {
    return (text.length() > 24 ? text.substring(0, 22) + "..." : text);
  }

  public static void putTitle(String title) {
    lines();
    System.out.printf("%95s", title + "\n");
    lines();
  }

  public static void lines() {
    System.out.println(
        "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
  }

  public static void shortLines() {
    System.out.println("----------------------------------------------");
  }

  public static String getNameClass(Object obj) {
    String className =
        obj.getClass().getName().substring(obj.getClass().getName().lastIndexOf('.') + 1);
    return className.substring(0, className.indexOf('R'));
  }
}
