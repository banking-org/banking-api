package postgres.addict;

public class Utils {
  public static String spaceToSnakeCase(String value){
    return value.trim().replace(" ", "_");
  }
}
