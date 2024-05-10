package ru.practicum.admin.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.dto.UpdateCompilationRequest;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.storage.CompilationStorage;
import ru.practicum.storage.EventStorage;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCompilationService {

    private final EventStorage eventStorage;
    private final CompilationStorage compilationStorage;

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            if (!newCompilationDto.getEvents().isEmpty()) {
                for (Long id : newCompilationDto.getEvents()) {
                    compilation.getEvents().add(eventStorage.findById(id).get());
                }
            }
        }
        compilationStorage.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        compilationStorage.deleteById(compId);
    }

    @Transactional
    public void deleteEventInCompilation(Long compId, Long eventId) {
        Compilation compilation = compilationStorage.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Compilation with id=%d was not found.", compId)));
        compilation.getEvents().removeIf(e -> (e.getId().equals(eventId)));
        compilationStorage.saveAndFlush(compilation);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compRequest) {
        Compilation compilation = compilationStorage.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Compilation with id=%d was not found.", compId)));
        if (compRequest.getEvents() != null) {
            List<Event> events = eventStorage.findAllByIdIn(new HashSet<>(compRequest.getEvents()));
            compilation.setEvents(events);
        }
        if (compRequest.getTitle() != null) {
            compilation.setTitle(compRequest.getTitle());
        }
        if (compRequest.getPinned() != null) {
            compilation.setPinned(compRequest.getPinned());
        }
        compilation = compilationStorage.save(compilation);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void updateEventInCompilation(Long compId, Long eventId) {
        Compilation compilation = compilationStorage.findById(compId).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Compilation with id=%d was not found.", compId)));
        Event event = eventStorage.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Event with id=%d was not found.", eventId)));
        compilation.getEvents().add(event);
        compilationStorage.saveAndFlush(compilation);
    }
}