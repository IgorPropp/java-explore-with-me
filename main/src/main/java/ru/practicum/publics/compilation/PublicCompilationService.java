package ru.practicum.publics.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;

import org.springframework.data.domain.Pageable;
import ru.practicum.storage.CompilationStorage;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCompilationService {

    private final CompilationStorage compilationStorage;

    public List<CompilationDto> getCompilations(boolean pinned, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from, size);
        if (pinned) {
            return CompilationMapper.toListCompilationDto(compilationStorage.findByPinned(pinned, pageable)
                    .getContent());
        } else {
            return CompilationMapper.toListCompilationDto(compilationStorage.findAll(pageable).getContent());
        }
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationStorage.findById(compId).orElseThrow(() ->
                new ObjectNotFoundException("Объект не найден. ",
                        String.format("Compilation with id=%d was not found.", compId)));
        return CompilationMapper.toCompilationDto(compilation);
    }
}