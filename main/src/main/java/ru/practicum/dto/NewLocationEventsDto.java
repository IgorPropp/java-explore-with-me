package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewLocationEventsDto {

    @NotBlank
    private String name;
    @NotNull
    private Long lat;
    @NotNull
    private Long lon;
    @NotNull
    private Long rad;

}