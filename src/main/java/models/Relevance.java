package models;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Relevance {
  NONE,
  LOW,
  MEDIUM,
  HIGH;
  static final Map<String, Relevance> relevanceMap = new LinkedHashMap<>();

  public static Map<String, Relevance> getRelevanceMap() {
    relevanceMap.put("NONE", Relevance.NONE);
    relevanceMap.put("LOW", Relevance.LOW);
    relevanceMap.put("MEDIUM", Relevance.MEDIUM);
    relevanceMap.put("HIGH", Relevance.HIGH);
    return relevanceMap;
  }
}
