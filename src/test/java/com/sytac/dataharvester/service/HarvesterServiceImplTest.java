package com.sytac.dataharvester.service;

import com.sytac.dataharvester.mapper.EventMapper;
import com.sytac.dataharvester.model.response.HarvesterResponse;
import com.sytac.dataharvester.model.response.UserDigestInfo;
import com.sytac.dataharvester.model.sse.EventData;
import jdk.jfr.Description;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import java.time.Duration;

import static com.sytac.dataharvester.service.Fixture.generateSampleSSE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HarvesterServiceImplTest {

    public static final int DATA_PRODUCTION_DURATION_MILLIS = 100;
    @Autowired
    HarvesterServiceImpl harvesterService;
    ServerSentEvent<EventData> ServerSentEventForSytflix;
    ServerSentEvent<EventData> ServerSentEventForSytazon;
    ServerSentEvent<EventData> ServerSentEventForSysney;

    private WebClient webClient1;
    private WebClient webClient2;
    private WebClient webClient3;

    ParameterizedTypeReference<ServerSentEvent<EventData>> type = new ParameterizedTypeReference<>() {
    };


    private void setUpScenarioOne_OneUserStartOneShowOnALLThreePlatforms() {
        webClient1 = setUpSytflixWebClient(1, 100, "sytflix", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        webClient2 = setUpSytazonWebClient(1, 100,"sytazon", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        WebClient webClient3 = setUpSysneyWebClient(1, 100,"sysney", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        harvesterService = new HarvesterServiceImpl(webClient1, webClient2, webClient3, new EventMapper());
    }

    private void setUpScenarioTwo_OneUserStartOneShowFiveTimesOnALLThreePlatforms() {
        webClient1 = setUpSytflixWebClient(5, 100, "sytflix", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        webClient2 = setUpSytazonWebClient(5, 100,"sytazon", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        webClient3 = setUpSysneyWebClient(5, 100,"sysney", DATA_PRODUCTION_DURATION_MILLIS, "Amirhossein", "Farmad", "12/04/2003");
        harvesterService = new HarvesterServiceImpl(webClient1, webClient2, webClient3, new EventMapper());
    }

    private void setUpScenarioThree_UserNamedSytacStartShowSeveralTimesOnALLThreePlatforms() {
        webClient1 = setUpSytflixWebClient(20, 100, "sytflix", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        webClient2 = setUpSytazonWebClient(30, 100,"sytazon", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        webClient3 = setUpSysneyWebClient(40, 100,"sysney", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        harvesterService = new HarvesterServiceImpl(webClient1, webClient2, webClient3, new EventMapper());
    }

    private void setUpScenarioFour_UserNamedSytacStartShowSeveralTimesOnALLThreePlatforms() {
        webClient1 = setUpSytflixWebClient(20, 100, "sytflix", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        webClient2 = setUpSytazonWebClient(0, 100,"sytazon", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        webClient3 = setUpSysneyWebClient(0, 100,"sysney", DATA_PRODUCTION_DURATION_MILLIS, "Sytac", "Sytac", "12/04/2003");
        harvesterService = new HarvesterServiceImpl(webClient1, webClient2, webClient3, new EventMapper());
    }


    @NotNull
    private WebClient setUpSysneyWebClient(int numberOfEvents, int dataProductionPeriodInMillis, String platform, int userId, String firstName, String lastname, String dob) {
        ServerSentEventForSysney = generateSampleSSE( platform,  userId,  firstName,  lastname,  dob);
        Flux<ServerSentEvent<EventData>> sseFluxForSysney = getServerSentEventFlux(numberOfEvents, dataProductionPeriodInMillis, ServerSentEventForSysney);
        WebClient webClient3 = getWebClient(type, sseFluxForSysney);
        return webClient3;
    }

    @NotNull
    private WebClient setUpSytazonWebClient(int numberOfEvents, int dataProductionPeriodInMillis,String platform, int userId, String firstName, String lastname, String dob) {
        ServerSentEventForSytazon = generateSampleSSE(platform,  userId,  firstName,  lastname,  dob);
        Flux<ServerSentEvent<EventData>> sseFluxForSytazon = getServerSentEventFlux(numberOfEvents, dataProductionPeriodInMillis, ServerSentEventForSytazon);
        WebClient webClient2 = getWebClient(type, sseFluxForSytazon);
        return webClient2;
    }

    @NotNull
    private WebClient setUpSytflixWebClient(int numberOfEvents, int dataProductionPeriodInMillis, String platform, int userId, String firstName, String lastname, String dob) {
        ServerSentEventForSytflix = generateSampleSSE(platform,  userId,  firstName,  lastname,  dob);
        Flux<ServerSentEvent<EventData>> sseFluxForSytflix = getServerSentEventFlux(numberOfEvents, dataProductionPeriodInMillis,ServerSentEventForSytflix);
        WebClient webClient1 = getWebClient(type, sseFluxForSytflix);
        return webClient1;
    }

    @NotNull
    private Flux<ServerSentEvent<EventData>> getServerSentEventFlux(int numberOfEvents, int dataProductionPeriodInMillis, ServerSentEvent<EventData> serverSentEvent) {
        return Flux.interval(Duration.ofMillis(dataProductionPeriodInMillis))
                .map(i -> ServerSentEvent.<EventData>builder().data(serverSentEvent.data()).build())
                .take(numberOfEvents);
    }


    @NotNull
    private WebClient getWebClient(ParameterizedTypeReference<ServerSentEvent<EventData>> type, Flux<ServerSentEvent<EventData>> serverSentEventFlux) {
        WebClient webClient1 = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock1 = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpecMock1 = mock(WebClient.ResponseSpec.class);
        when(webClient1.get()).thenReturn(requestHeadersUriSpecMock1);
        when(requestHeadersUriSpecMock1.retrieve()).thenReturn(responseSpecMock1);
        when(responseSpecMock1.bodyToFlux(type)).thenReturn(serverSentEventFlux);
        return webClient1;
    }


    @Test
    void oneUserStartOneShowOnALLThreePlatforms_userActivityIsThree() {
        setUpScenarioOne_OneUserStartOneShowOnALLThreePlatforms();

        HarvesterResponse result = harvesterService.doHarvestMultiplePlatform();

        final var expectedUser = new UserDigestInfo(100, "Amirhossein Farmad", 20);

        assertNotNull(result);
        assertTrue(result.getUserActivity().containsKey(expectedUser));
        assertEquals(3, result.getUserActivity().get(expectedUser).size());
    }


    @Test
    @Description("3 platform shows 5 shows released in/after 2020")
    void oneUserStartOneShowFiveTimesOnALLThreePlatforms_showsReleasedInOrAfter2020isThree() {
        setUpScenarioTwo_OneUserStartOneShowFiveTimesOnALLThreePlatforms();

        HarvesterResponse result = harvesterService.doHarvestMultiplePlatform();

        assertEquals(3*5, result.getPlatformStatistics().get("SHOWS_RELEASED_IN_2020_OR_LATER"));
    }

    @Test
    @Description("Reading from each 3 platform will be stopped at the third occurrence of a user with first name Sytac. So Sytac reads 2 events on 3 platforms.")
    void userNamedSytacStartOneShowFiveTimesOnALLThreePlatforms_showsReleasedInOrAfter2020isSix() {
        setUpScenarioThree_UserNamedSytacStartShowSeveralTimesOnALLThreePlatforms();

        HarvesterResponse result = harvesterService.doHarvestMultiplePlatform();

        assertEquals(3*2, result.getPlatformStatistics().get("SHOWS_RELEASED_IN_2020_OR_LATER"));
    }


    @Test
    @Description("Reading from 1 platform will be stopped at the third occurrence of a user with first name Sytac.")
    void userNamedSytacStartOneShowSeveralTimesOnOnePlatforms_readingStreamIsStoppedAtThreeOccurrence() {
        setUpScenarioFour_UserNamedSytacStartShowSeveralTimesOnALLThreePlatforms();

        Flux<ServerSentEvent<EventData>> result = harvesterService.emitAndCashPlatformData(webClient1);
        StepVerifier.create(result)
                .expectNextMatches(event -> event.data().getUser().getFirstName().equals("Sytac"))
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

}


