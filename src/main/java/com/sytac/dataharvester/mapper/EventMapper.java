package com.sytac.dataharvester.mapper;


import com.sytac.dataharvester.model.response.EventDigestInfo;
import com.sytac.dataharvester.model.response.UserDigestInfo;
import com.sytac.dataharvester.model.sse.EventData;
import com.sytac.dataharvester.model.sse.Show;
import com.sytac.dataharvester.model.sse.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.sytac.dataharvester.util.DateTimeUtil.convertTime;
import static com.sytac.dataharvester.util.StringUtils.getFirstElement;

@Slf4j
@Component
public class EventMapper {

    private static final String TARGET_TIME_ZONE = "CET";

    public EventDigestInfo mapServerSentEventToEventDigestInfo(ServerSentEvent<EventData> sse) {
        return Optional.ofNullable(sse)
                .map(ServerSentEvent::data)
                .map(data -> createEventDigestInfo(sse, data))
                .orElseGet( ()->{
                    log.error("event data for event id {} is null!", sse.id());
                    return null;
                });
    }

    private EventDigestInfo createEventDigestInfo(ServerSentEvent<EventData> sse, EventData data) {
        Show show = data.getShow();
        User user = data.getUser();

        if (show == null || user == null) {
            log.error("event.data.show or event.data.user for event id {} is null!", sse.id());
            return null;
        }

        UserDigestInfo userDigestInfo = createUserDigestInfo(user);

        return EventDigestInfo.builder()
                .eventPlatform(show.getPlatform())
                .eventName(sse.event())
                .eventDate(convertTime(data.getEventDate(), user.getCountry(), TARGET_TIME_ZONE))
                .showId(show.getShowId())
                .showTitle(show.getTitle())
                .showFirstCast(getFirstElement(show.getCast()))
                .userDigestInfo(userDigestInfo)
                .build();
    }

    private UserDigestInfo createUserDigestInfo(User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();
        int age = calculateAge(user.getDateOfBirth());

        return new UserDigestInfo(user.getId(), fullName, age);
    }

    private int calculateAge(String dateOfBirth){
        LocalDate dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Period period = Period.between(dob, LocalDate.now());
        return period.getYears();
    }
}
