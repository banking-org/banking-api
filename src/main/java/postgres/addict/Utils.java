package postgres.addict;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Utils {
  public static String spaceToSnakeCase(String value){
    return value.trim().replace(" ", "_");
  }

  public static String objectToPsqlValue(Object value){
    if(value instanceof String || value instanceof Character) {
      return "'" + value + "'";
    }
    if(value instanceof Enum<?>){
      return "'" + ((Enum<?>) value).name() + "'";
    }
    if(value instanceof LocalDate){
      return "'" + Date.valueOf((LocalDate) value) + "'::date";
    }
    if(value instanceof Instant) {
      return "'" + Timestamp.from((Instant) value) + "'::timestamp";
    }
    if(value instanceof LocalDateTime){
      return "'" + Timestamp.valueOf((LocalDateTime) value) + "'::timestamp";
    }
    if(value instanceof LocalTime){
      return "'" + Time.valueOf((LocalTime) value) + "'::time";
    }
    return value.toString();
  }
}
