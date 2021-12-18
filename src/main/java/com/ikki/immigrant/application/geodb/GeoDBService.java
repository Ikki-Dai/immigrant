package com.ikki.immigrant.application.geodb;

import com.maxmind.db.CHMCache;
import com.maxmind.db.NodeCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * @author ikki
 */
@Service
@Slf4j
public class GeoDBService {

    private final File database;
    private final NodeCache cache = new CHMCache();
    private DatabaseReader reader;

    public GeoDBService(@Value("${geo.db-path:#{T(java.lang.System).getProperty('java.io.tmpdir')}}") String tempPath) throws IOException {
        this.database = new File(new File(tempPath), "GeoLite2-City.mmdb");
        if (database.exists()) {
            reader = new DatabaseReader.Builder(database)
                    .withCache(cache)
                    .build();
        }
    }

    protected CityResponse queryCityByIp(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            return reader.city(ipAddress);
        } catch (IOException | GeoIp2Exception e) {
            log.warn("can not found from ip: {}  with message: {}", ip, e.getMessage());
        }
        return null;
    }

    public GeoLocation queryByLocale(String ip, String locale) {
        GeoLocation geoLocation = new GeoLocation();
        Map<String, String> names;
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            geoLocation.setIp(ip);
            CityResponse cityResponse = reader.city(ipAddress);

            // country
            names = cityResponse.getCountry().getNames();
            String countryName = names.get(locale);
            if (!StringUtils.hasLength(countryName)) {
                countryName = cityResponse.getCountry().getName();
            }
            geoLocation.setCountry(countryName);
            geoLocation.setIsoCode(cityResponse.getCountry().getIsoCode());
            // city
            names = cityResponse.getCity().getNames();
            String cityName = names.get(locale);
            if (!StringUtils.hasLength(cityName)) {
                cityName = cityResponse.getCity().getName();
            }
            geoLocation.setCity(cityName);

            geoLocation.setLatitude(cityResponse.getLocation().getLatitude());
            geoLocation.setLongitude(cityResponse.getLocation().getLongitude());

        } catch (IOException | GeoIp2Exception e) {
            log.warn("can not found from ip: {} and locale: {} with message: {}", ip, locale, e.getMessage());
        }
        return geoLocation;
    }


    protected synchronized void reload() {
        try {
            if (null != reader) {
                reader.close();
            }
            reader = new DatabaseReader.Builder(database)
                    .withCache(cache)
                    .build();
        } catch (IOException e) {
            log.error("reloadDB for update error : {}", e.getMessage());
        }
    }
}
