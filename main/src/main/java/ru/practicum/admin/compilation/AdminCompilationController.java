package ru.practicum.admin.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return adminCompilationService.addCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest compRequest) {

        return adminCompilationService.updateCompilation(compId, compRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        adminCompilationService.deleteCompilation(compId);
    }

    @DeleteMapping("/{compId}/events/{eventId}")
    public void deleteEventInCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        adminCompilationService.deleteEventInCompilation(compId, eventId);
    }

    @PatchMapping("/{compId}/events/{eventId}")
    public void updateEventInCompilation(@PathVariable Long compId, @PathVariable Long eventId) {
        adminCompilationService.updateEventInCompilation(compId, eventId);
    }

//    @DeleteMapping("/{compId}/pin")
//    public void unpinCompilation(@PathVariable Long compId) {
//        adminCompilationService.unpinCompilationById(compId);
//    }
//
//    @PatchMapping("/{compId}/pin")
//    public void pinCompilation(@PathVariable Long compId) {
//        adminCompilationService.pinCompilationById(compId);
//    }
}