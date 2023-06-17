package utils;

import models.Relevance;
import models.RepeatType;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class AskForMethodsDataContainer {
    private static final Map<String, RepeatType> repeatTypeMap = new LinkedHashMap<>();
    private static final Object[] repeatTypeArray = getRepeatTypeMap().keySet().toArray();
    private static final Map<String, Relevance> relevanceMap = new LinkedHashMap<>();
    private static final Object[] relevanceArray = getRelevanceMap().keySet().toArray();
    private static final Map<String, DayOfWeek> daysOfWeekMap = new LinkedHashMap<>();
    private static final Object[] daysOfWeekArray = getDaysOfWeekMap().keySet().toArray();
    private static Object[] daysOfMonthOptionsArrayStr;
    private static Object[] minuteOptionsArrayObj;

    private AskForMethodsDataContainer() {}

    public static Object[] getRepeatTypeArray(){
        return repeatTypeArray;
    }
    public static Object[] getRelevanceArray(){
        return relevanceArray;
    }
    public static Object[] getDaysOfWeekArray(){
        return daysOfWeekArray;
    }

    public static Map<String, RepeatType> getRepeatTypeMap() {
        if (repeatTypeMap.isEmpty()) {
            repeatTypeMap.put("HOUR", RepeatType.HOUR);
            repeatTypeMap.put("DAILY", RepeatType.DAILY);
            repeatTypeMap.put("WEEKLY", RepeatType.WEEKLY);
            repeatTypeMap.put("MONTHLY", RepeatType.MONTHLY);
            repeatTypeMap.put("YEARLY", RepeatType.YEARLY);
        }
        return repeatTypeMap;
    }

    public static Map<String, Relevance> getRelevanceMap() {
        if (relevanceMap.isEmpty()) {
            relevanceMap.put("NONE", Relevance.NONE);
            relevanceMap.put("LOW", Relevance.LOW);
            relevanceMap.put("MEDIUM", Relevance.MEDIUM);
            relevanceMap.put("HIGH", Relevance.HIGH);
        }
        return relevanceMap;
    }

    public static Map<String, DayOfWeek> getDaysOfWeekMap() {
        if (daysOfWeekMap.isEmpty()) {
            daysOfWeekMap.put("MONDAY", DayOfWeek.MONDAY);
            daysOfWeekMap.put("TUESDAY", DayOfWeek.TUESDAY);
            daysOfWeekMap.put("WEDNESDAY", DayOfWeek.WEDNESDAY);
            daysOfWeekMap.put("THURSDAY", DayOfWeek.THURSDAY);
            daysOfWeekMap.put("FRIDAY", DayOfWeek.FRIDAY);
            daysOfWeekMap.put("SATURDAY", DayOfWeek.SATURDAY);
            daysOfWeekMap.put("SUNDAY", DayOfWeek.SUNDAY);
        }
        return daysOfWeekMap;
    }

    public static Object[] getMinuteOptionsArrayObj() {
        if (minuteOptionsArrayObj == null) {
            AtomicInteger ai = new AtomicInteger(0);
            int[] minutesOptionsArray = IntStream.generate(ai::getAndIncrement).limit(60).toArray();
            minuteOptionsArrayObj = Arrays.stream(minutesOptionsArray).boxed().toArray();
        }
        return minuteOptionsArrayObj;
    }

    public static Object[] getDaysOfMonthOptionsArrayStr() {
        if (daysOfMonthOptionsArrayStr == null) {
            AtomicInteger ai = new AtomicInteger(1);
            int[] daysOfMonthOptionsArray = IntStream.generate(ai::getAndIncrement).limit(31).toArray();
            daysOfMonthOptionsArrayStr = Arrays.stream(daysOfMonthOptionsArray).boxed().toArray();
        }
        return daysOfMonthOptionsArrayStr;
    }

}
