package postgres.addict;


public class TypeMapper {
  public static final String NO_TYPE_MAPPED = "text";

  public static String javaToPsql(String type) {
    return switch (type.toLowerCase()) {
      case "boolean" -> "boolean";
      case "int", "integer" -> "integer";
      case "long", "biginteger", "bigint" -> "bigint";
      case "float" -> "float";
      case "double" -> "double precision";
      case "char", "character" -> "char";
      case "string", "charsequence" -> "varchar";
      case "date", "localdate" -> "date";
      case "time", "localtime" -> "time";
      case "timestamp", "localdatetime", "instant" -> "timestamp";
      default -> NO_TYPE_MAPPED;
    };
  }

  public static String typeToSequenceIfPossible(String type) {
    return switch (type) {
      case ColumnType.INT2 -> "smallserial";
      case ColumnType.BIGINT, ColumnType.INT8 -> "bigserial";
      case ColumnType.INTEGER, ColumnType.INT4 -> "serial";
      default -> type;
    };
  }
}
