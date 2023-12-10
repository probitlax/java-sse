package com.sytac.dataharvester.service;

import com.sytac.dataharvester.mapper.EventMapper;
import com.sytac.dataharvester.model.response.EventDigestInfo;
import com.sytac.dataharvester.model.response.HarvesterResponse;
import com.sytac.dataharvester.model.response.UserDigestInfo;
import com.sytac.dataharvester.model.sse.EventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HarvesterServiceImpl implements HarvesterService {

    public static final int EMIT_DURATION_IN_SECONDS = 5;
    public static final String SYTAC_USER_FIRST_NAME = "Sytac";
    public static final int SYTAC_USER_MAX_OCCURRENCE = 3;
    public static final int SHOW_RELEASE_YEAR_CONDITION = 2020;

    private final WebClient sytflixWebClient;
    private final WebClient sytazonWebClient;
    private final WebClient sysneyWebClient;
    private final EventMapper eventMapper;
    private final ParameterizedTypeReference<ServerSentEvent<EventData>> type = new ParameterizedTypeReference<>() {
    };
    private Map<String, Integer> platformStatistics = new TreeMap<>();

    public HarvesterResponse doHarvestMultiplePlatform() {
        var sytflixSSE = emitAndCashPlatformData(sytflixWebClient);
        var sytazonSSE = emitAndCashPlatformData(sytazonWebClient);
        var sysneySSE = emitAndCashPlatformData(sysneyWebClient);

        var mergedSSE = mergeAndCacheMultiplePlatformData(sytflixSSE, sytazonSSE, sysneySSE);

        setStatistics(sytflixSSE, sytazonSSE, sysneySSE, mergedSSE);

        Map<UserDigestInfo, List<EventDigestInfo>> groupedData = groupByUser(mergedSSE);

        return new HarvesterResponse(platformStatistics, groupedData);
    }

    private Map<UserDigestInfo, List<EventDigestInfo>> groupByUser(Flux<ServerSentEvent<EventData>> mergedSSE) {
        final var blockedSSE = mergedSSE.map(eventMapper::mapServerSentEventToEventDigestInfo)
                .collectList()
                .block();

        if (blockedSSE==null || blockedSSE.isEmpty()) throw new IllegalArgumentException();

        return blockedSSE.stream()
                .collect(Collectors.groupingBy(EventDigestInfo::getUserDigestInfo));
    }


    private Flux<ServerSentEvent<EventData>> mergeAndCacheMultiplePlatformData(Flux<ServerSentEvent<EventData>> sytflixSSE, Flux<ServerSentEvent<EventData>> sytazonSSE, Flux<ServerSentEvent<EventData>> sysneySSE) {
        return sytflixSSE.mergeWith(sytazonSSE).mergeWith(sysneySSE).cache();
    }

    private void setStatistics(Flux<ServerSentEvent<EventData>> sytflixSSE, Flux<ServerSentEvent<EventData>> sytazonSSE, Flux<ServerSentEvent<EventData>> sysneySSE, Flux<ServerSentEvent<EventData>> mergedSSE) {
        platformStatistics.put("SYTFLIX_ELAPSED_TIME_MS", getElapsedTime(sytflixSSE));
        platformStatistics.put("STAZON_ELAPSED_TIME_MS", getElapsedTime(sytazonSSE));
        platformStatistics.put("SYSNEY_ELAPSED_TIME_MS", getElapsedTime(sysneySSE));
        platformStatistics.put("SHOWS_RELEASED_IN_2020_OR_LATER", getTotalNumberOfShowsReleasedIn2020OrLater(mergedSSE));
    }

    @VisibleForTesting
    Flux<ServerSentEvent<EventData>> emitAndCashPlatformData(WebClient platformWebClient) {
        AtomicInteger userActivityCounter = new AtomicInteger(0);
        return platformWebClient.get()
                .retrieve()
                .bodyToFlux(type)
                .take(EMIT_DURATION_IN_SECONDS)
                .takeWhile(event -> {
                    if (SYTAC_USER_FIRST_NAME.equals(event.data().getUser().getFirstName())) {
                        int currentCount = userActivityCounter.incrementAndGet();
                        log.info("user {} did an activity on {} platform for the {} time!", SYTAC_USER_FIRST_NAME, event.data().getShow().getPlatform(), currentCount);
                        return currentCount < SYTAC_USER_MAX_OCCURRENCE;
                    }
                    return true;
                })
                .onErrorContinue((throwable, o) -> log.error("Error occurred but continuing: {}", throwable.getMessage()))
                .cache();
    }

    private Integer getElapsedTime(Flux<ServerSentEvent<EventData>> sse) {
        return sse.elapsed().map(tuple -> tuple.getT1().intValue()).blockLast();
    }

    private Integer getTotalNumberOfShowsReleasedIn2020OrLater(Flux<ServerSentEvent<EventData>> sse) {


        final var blockedSEE = sse.filter(event -> {
            final var show = event.data().getShow();
            if (show.getReleaseYear() > SHOW_RELEASE_YEAR_CONDITION) {
                log.info("Show \"{}\" was released in {}.", show.getTitle(), show.getReleaseYear());
                return true;
            }
            return false;
        })
                .count()
                .doOnSuccess(counter -> log.info("{} show(s) was/were released in/after 2020.", counter))
                .block();

        return (blockedSEE==null) ? 0 : blockedSEE.intValue();
    }
}
