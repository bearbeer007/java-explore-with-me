package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHitDto;
import ru.practicum.model.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;


    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("StatisticController, saveHit, Request body app: {}, uri: {}, ip: {}, timestamp: {}",
                endpointHitDto.getApp(), endpointHitDto.getUri(), endpointHitDto.getIp(), endpointHitDto.getTimestamp());
        statsService.saveHit(endpointHitDto);
        return new ResponseEntity<>(endpointHitDto, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestParam(value = "start", required = false) String startStr,
            @RequestParam(value = "end", required = false) String endStr,
            @RequestParam(required = false, value = "uris") List<String> uris,
            @RequestParam(required = false, value = "unique") boolean unique
    ) {
        log.info("Statistic Controller, getStats, parameters: start {}, end {}, uris {}, unique {}",
                startStr, endStr, uris, unique);

        LocalDateTime start, end;

        try {
            if (startStr == null || endStr == null) {
                return ResponseEntity.badRequest().body("Start and end parameters are required");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            start = LocalDateTime.parse(startStr, formatter);
            end = LocalDateTime.parse(endStr, formatter);
        } catch (DateTimeParseException e) {
            log.error("Error parsing date: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid date format. Expected format: " + DATE_FORMAT);
        }

        List<ViewStatsDto> statsList = statsService.findHitsByParams(start, end, uris, unique);
        return ResponseEntity.ok(statsList);
    }

}
