package com.sytac.dataharvester.model.sse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {
    private int id;
    @JsonProperty(value = "date_of_birth")
    private String dateOfBirth;
    private String email;
    @JsonProperty(value = "first_name")
    private String firstName;
    private String gender;
    @JsonProperty(value = "ip_address")
    private String ipAddress;
    private String country;
    @JsonProperty(value = "last_name")
    private String lastName;
}
