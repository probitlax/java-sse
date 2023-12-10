package com.sytac.dataharvester.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class HarvesterResponse {
    private Map<String, Integer> platformStatistics;
    private Map<UserDigestInfo, List<EventDigestInfo>> userActivity;
}
