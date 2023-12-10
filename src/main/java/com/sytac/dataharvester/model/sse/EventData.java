package com.sytac.dataharvester.model.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventData {
    private Show show;
    @JsonProperty(value = "event_date")
    private String eventDate;
    private User user;
}
