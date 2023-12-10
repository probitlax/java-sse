package com.sytac.dataharvester.service;

import com.sytac.dataharvester.model.sse.EventData;
import com.sytac.dataharvester.model.sse.Show;
import com.sytac.dataharvester.model.sse.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.ServerSentEvent;

public class Fixture {



    static ServerSentEvent<EventData> generateSampleSSE(String platform, int userId, String firstName, String lastname, String dob){
        User user = generateSampleUser(userId, firstName, lastname, dob);
        Show show = generateSampleShow(platform);
        EventData eventData = generateSampleEventData(user, show);

        return ServerSentEvent.<EventData>builder()
                .id("be0e2b55-f6ac-464b-bfdb-5e6e424db0e5")
                .event("stream-started")
                .data(eventData)
                .build();
    }

    @NotNull
    private static User generateSampleUser(int userId, String firstName, String lastname, String dob) {
        User user = new User();
        user.setId(userId);
        user.setDateOfBirth(dob);
        user.setEmail("firstname1@gmail.com");
        user.setFirstName(firstName);
        user.setLastName(lastname);
        user.setGender("Male");
        user.setIpAddress("153.132.77.6");
        user.setCountry("BR");
        return user;
    }

    @NotNull
    private static Show generateSampleShow(String platform) {
        Show show = new Show();
        show.setShowId("s7");
        show.setCast("Wason Pomoa, Garret Qojlahunt, Yill Nagner, Xfephen Lang, Pasha Xosoff, Tala Faker, Kxaser Iehrheson, Qeach Zkant, Xlenn Innis, Vodd Lcott, Pahn JcClarnon, Dmendan Rletcher");
        show.setCountry("United States");
        show.setDateAdded("May 29, 2021");
        show.setDirector("Esh Nlannon, Fhris Ruck");
        show.setDuration("90 min");
        show.setListedIn("Children & Family Movies");
        show.setRating("TV-MA");
        show.setReleaseYear(2023);
        show.setTitle("Vvhulz Taves Omerica");
        show.setType("Movie");
        show.setPlatform(platform);
        return show;
    }

    @NotNull
    public static EventData generateSampleEventData(User user, Show show) {
        EventData eventData = new EventData();
        eventData.setEventDate("26-11-2023 21:02:43.378");
        eventData.setShow(show);
        eventData.setUser(user);
        return eventData;
    }
}
