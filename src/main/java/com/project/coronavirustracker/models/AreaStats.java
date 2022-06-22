package com.project.coronavirustracker.models;

import lombok.Data;

@Data
public class AreaStats {

    private String province;
    private String country;
    private int latestTotalCases;
    private int tendency;

}
