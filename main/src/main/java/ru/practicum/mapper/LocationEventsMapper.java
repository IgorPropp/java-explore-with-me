package ru.practicum.mapper;

import ru.practicum.dto.LocationEventsDto;
import ru.practicum.dto.NewLocationEventsDto;
import ru.practicum.model.LocationEvents;

import java.util.ArrayList;
import java.util.List;

public class LocationEventsMapper {

    public static LocationEvents newToLocationEvents(NewLocationEventsDto locationEventsDto) {
        LocationEvents locationGroup = new LocationEvents();
        locationGroup.setName(locationEventsDto.getName());
        locationGroup.setLat(locationEventsDto.getLat());
        locationGroup.setLon(locationEventsDto.getLon());
        locationGroup.setRad(locationEventsDto.getRad());
        return locationGroup;

    }

    public static LocationEventsDto locationEventsToDto(LocationEvents locationGroup) {
        return new LocationEventsDto(
                locationGroup.getId(),
                locationGroup.getName(),
                locationGroup.getLat(),
                locationGroup.getLon(),
                locationGroup.getRad()
        );
    }

    public static List<LocationEventsDto> toListLocationEventsDto(List<LocationEvents> list) {
        List<LocationEventsDto> setDto = new ArrayList<>();
        for (LocationEvents locationEvents : list) {
            setDto.add(locationEventsToDto(locationEvents));
        }
        return setDto;
    }
}