package ru.practicum.admin.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST admin compilation {}", newCompilationDto);
        return adminCompilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest compRequest) {
        log.info("PATCH admin compilation id={}", compId);
        return adminCompilationService.updateCompilation(compId, compRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE admin compilation id={}", compId);
        adminCompilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventInCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("DELETE admin event id={} in compilation id={}", eventId, compId);
        adminCompilationService.deleteEventInCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void updateEventInCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        log.info("PATCH admin event id={} in compilation id={}", eventId, compId);
        adminCompilationService.updateEventInCompilation(compId, eventId);
    }
}