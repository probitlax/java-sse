package com.sytac.dataharvester.model.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Show {
    @JsonProperty(value = "show_id")
    private String showId;
    private String cast;
    private String country;
    @JsonProperty(value = "date_added")
    private String dateAdded;
    private String description;
    private String director;
    private String duration;
    @JsonProperty(value = "listed_in")
    private String listedIn;
    private String rating;
    @JsonProperty(value = "release_year")
    private int releaseYear;
    private String title;
    private String type;
    private String platform;
}
