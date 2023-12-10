package com.sytac.dataharvester.controller;

import com.sytac.dataharvester.model.response.HarvesterResponse;
import com.sytac.dataharvester.service.HarvesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/harvester")
@RequiredArgsConstructor
public class HarvesterController {

    private final HarvesterService harvesterService;

    @GetMapping("/run")
    public ResponseEntity<HarvesterResponse> run() {
        HarvesterResponse harvesterResponse = harvesterService.doHarvestMultiplePlatform();
        return ResponseEntity.ok(harvesterResponse);
    }

}
