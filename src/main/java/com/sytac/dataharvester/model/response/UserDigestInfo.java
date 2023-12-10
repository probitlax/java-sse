package com.sytac.dataharvester.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDigestInfo {
    private int userId;
    private String userFullName;
    private int userAge;
}
