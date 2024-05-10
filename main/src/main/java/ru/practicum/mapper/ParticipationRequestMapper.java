package ru.practicum.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(request.getCreated(), request.getEvent().getId(),
                request.getId(), request.getRequester().getId(), request.getStatus());
    }

    public static List<ParticipationRequestDto> toListParticipationRequestDto(List<ParticipationRequest> list) {
        List<ParticipationRequestDto> listDto = new ArrayList<>();
        for (ParticipationRequest request : list) {
            listDto.add(toParticipationRequestDto(request));
        }
        return listDto;
    }
}