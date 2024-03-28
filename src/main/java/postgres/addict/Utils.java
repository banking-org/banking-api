package postgres.addict;

import lombok.NonNull;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Utils {
  public static String stringAorB(String a, @NonNull String b){
    if(a == null){
      return b.trim();
    }
    String cleanedA = a.trim();
    if(cleanedA.isEmpty()){
      return b.trim();
    }
    return a.trim();
  }

  public static String toSnakeCase(String value){
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
