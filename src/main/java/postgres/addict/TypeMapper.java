package postgres.addict;

import postgres.addict.runtime.definition.ColumnDefinition;

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

  public static String typeLimiter(ColumnDefinition definition){
    String bruteType = definition.getColumnType();
    String baseType =
      (definition.getGenerative() == GenerativeValue.SEQUENCE)
        ? TypeMapper.typeToSequenceIfPossible(bruteType)
        : bruteType;
    String type = definition.isReferences() ? bruteType : baseType;
    return switch (type){
      case ColumnType.VARCHAR,
        ColumnType.BPCHAR,
        ColumnType.CHAR,
        ColumnType.BIT -> type + "(" + definition.getSize() + ")";
      case ColumnType.NUMERIC -> type + "(" + definition.getPrecision() + ", " + definition.getScale() + ")";
      default -> type;
    };
  }
}
