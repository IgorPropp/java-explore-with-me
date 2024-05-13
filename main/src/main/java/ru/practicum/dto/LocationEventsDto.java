package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEventsDto {

    private Long id;
    private String name;
    private Long lat;
    private Long lon;
    private Long rad;
}