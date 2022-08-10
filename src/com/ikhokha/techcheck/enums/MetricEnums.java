package com.ikhokha.techcheck.enums;

import java.util.stream.Stream;

public enum MetricEnums {

    MOVER("MOVER_MENTIONS", "MOVER"),
    SHAKER("SHAKER_MENTIONS", "SHAKER"),
    QUESTIONS("QUESTIONS", "?"),
    SPAM("SPAM", "HTTP");

    private final String metricName;
    private final String itemToBeFiltered;

    MetricEnums(String metricName, String itemToBeFiltered){
        this.metricName = metricName;
        this.itemToBeFiltered = itemToBeFiltered;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getItemToBeFiltered() {
        return itemToBeFiltered;
    }

    public static Stream<MetricEnums> stream() {
        return Stream.of(MetricEnums.values());
    }
}
