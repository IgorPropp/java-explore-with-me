package ru.practicum.admin.location_events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.LocationEventsDto;
import ru.practicum.dto.NewLocationEventsDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/locations")
@Slf4j
@RequiredArgsConstructor
public class AdminLocationEventsController {

    private final AdminLocationEventsService adminLocationEventsService;

    @PostMapping
    public LocationEventsDto createLocationEvents(@Valid @RequestBody NewLocationEventsDto locationEventsDto) {
        log.info("POST admin locationEvents={}", locationEventsDto);
        return adminLocationEventsService.createLocationEvents(locationEventsDto);
    }

    @DeleteMapping("/{id}")
    public void deleteLocationEvents(@PathVariable Long id) {
        log.info("POST admin locationEvents id={}", id);
        adminLocationEventsService.deleteLocationEvents(id);
    }

    @PatchMapping
    public LocationEventsDto patchLocationEvents(@Valid @RequestBody LocationEventsDto locationEventsDto) {
        log.info("PATCH admin locationEvents={}", locationEventsDto);
        return adminLocationEventsService.patchLocationEvents(locationEventsDto);
    }

    @GetMapping
    public List<LocationEventsDto> getAllLocationEvents() {
        log.info("GET admin all locationEvents");
        return adminLocationEventsService.getAllLocationEvents();
    }

    @GetMapping("/{id}")
    public LocationEventsDto getLocationEventsById(@PathVariable Long id) {
        log.info("GET admin locationEvents id={}", id);
        return adminLocationEventsService.getLocationEventsById(id);
    }

}