package com.sytac.dataharvester.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DateTimeUtil {

    private DateTimeUtil() {
    }

    private static final Map<String, String> COUNTRY_TIMEZONE_MAP;

    static {
        COUNTRY_TIMEZONE_MAP = new HashMap<>();
        COUNTRY_TIMEZONE_MAP.put("PT", "UTC");
        COUNTRY_TIMEZONE_MAP.put("CA", "America/Toronto");
        COUNTRY_TIMEZONE_MAP.put("US", "America/Los_Angeles");
        COUNTRY_TIMEZONE_MAP.put("RU", "Europe/Moscow");
        COUNTRY_TIMEZONE_MAP.put("ID", "Asia/Jakarta");
        COUNTRY_TIMEZONE_MAP.put("CN", "Asia/Shanghai");
        COUNTRY_TIMEZONE_MAP.put("AU", "Australia/Sydney");
        COUNTRY_TIMEZONE_MAP.put("BR", "America/Sao_Paulo");
        COUNTRY_TIMEZONE_MAP.put("DE", "Europe/Berlin");
        COUNTRY_TIMEZONE_MAP.put("IN", "Asia/Kolkata");
        COUNTRY_TIMEZONE_MAP.put("JP", "Asia/Tokyo");
        COUNTRY_TIMEZONE_MAP.put("EG", "Africa/Cairo");
        COUNTRY_TIMEZONE_MAP.put("SA", "Asia/Riyadh");
        COUNTRY_TIMEZONE_MAP.put("MX", "America/Mexico_City");
        COUNTRY_TIMEZONE_MAP.put("ZA", "Africa/Johannesburg");
        COUNTRY_TIMEZONE_MAP.put("IT", "Europe/Rome");
        COUNTRY_TIMEZONE_MAP.put("ES", "Europe/Madrid");
        COUNTRY_TIMEZONE_MAP.put("GB", "Europe/London");
        COUNTRY_TIMEZONE_MAP.put("FR", "Europe/Paris");
        COUNTRY_TIMEZONE_MAP.put("AR", "America/Argentina/Buenos_Aires");
        COUNTRY_TIMEZONE_MAP.put("CL", "Chile/Continental");
        COUNTRY_TIMEZONE_MAP.put("CO", "America/Bogota");
        COUNTRY_TIMEZONE_MAP.put("PE", "America/Lima");
        COUNTRY_TIMEZONE_MAP.put("VE", "America/Caracas");
        COUNTRY_TIMEZONE_MAP.put("NG", "Africa/Lagos");
        COUNTRY_TIMEZONE_MAP.put("PK", "Asia/Karachi");
        COUNTRY_TIMEZONE_MAP.put("BD", "Asia/Dhaka");
        COUNTRY_TIMEZONE_MAP.put("IR", "Asia/Tehran");
        COUNTRY_TIMEZONE_MAP.put("TH", "Asia/Bangkok");
        COUNTRY_TIMEZONE_MAP.put("PH", "Asia/Manila");
        COUNTRY_TIMEZONE_MAP.put("VN", "Asia/Ho_Chi_Minh");
        COUNTRY_TIMEZONE_MAP.put("MY", "Asia/Kuala_Lumpur");
        COUNTRY_TIMEZONE_MAP.put("NL", "Europe/Amsterdam");
        COUNTRY_TIMEZONE_MAP.put("BE", "Europe/Brussels");
        COUNTRY_TIMEZONE_MAP.put("CH", "Europe/Zurich");
        COUNTRY_TIMEZONE_MAP.put("SE", "Europe/Stockholm");
        COUNTRY_TIMEZONE_MAP.put("AT", "Europe/Vienna");
        COUNTRY_TIMEZONE_MAP.put("NZ", "Pacific/Auckland");
        COUNTRY_TIMEZONE_MAP.put("AF", "Asia/Kabul");
        COUNTRY_TIMEZONE_MAP.put("SS", "Africa/Juba");
        COUNTRY_TIMEZONE_MAP.put("KP", "Asia/Pyongyang");
        COUNTRY_TIMEZONE_MAP.put("MG", "Indian/Antananarivo");
        COUNTRY_TIMEZONE_MAP.put("LT", "Europe/Vilnius");
        COUNTRY_TIMEZONE_MAP.put("PL", "Europe/Warsaw");
        COUNTRY_TIMEZONE_MAP.put("CZ", "Europe/Prague");
        COUNTRY_TIMEZONE_MAP.put("SI", "Europe/Ljubljana");
        COUNTRY_TIMEZONE_MAP.put("NA", "Africa/Windhoek");
    }


    public static String convertTime(String inputDateTime, String sourceCountry, String targetTimeZone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
        LocalDateTime dateTime = LocalDateTime.parse(inputDateTime, formatter);
        String zoneId = findZoneIdByCountryName(sourceCountry);
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of(zoneId));
        ZonedDateTime cetTime = zonedDateTime.withZoneSameInstant(ZoneId.of(targetTimeZone));
        return cetTime.format(formatter);
    }

    private static String findZoneIdByCountryName(String countryShortName) {
        return Optional.ofNullable(COUNTRY_TIMEZONE_MAP.get(countryShortName))
                .orElseThrow(() -> {
                    log.error("Timezone for " + countryShortName + " not found.");
                    return new IllegalArgumentException("Timezone not found for country: " + countryShortName);
                });
    }
}
