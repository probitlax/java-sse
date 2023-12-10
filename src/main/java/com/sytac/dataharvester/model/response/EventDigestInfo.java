package com.sytac.dataharvester.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class EventDigestInfo {
    private String eventPlatform;
    private String eventName;
    private String eventDate;

    @JsonIgnore
    private UserDigestInfo userDigestInfo;

    private String showId;
    private String showTitle;
    private String showFirstCast;
}
