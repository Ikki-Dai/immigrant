package com.ikki.immigrant.application.geodb;

import lombok.Getter;
import lombok.Setter;

/**
 * @author ikki
 */
@Getter
@Setter
public class GeoLocation {

    private String isoCode;
    private String country;
    private String city;
    private String ip;
    private Double latitude;
    private Double longitude;
}
