package ru.practicum.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;
    private List<Long> events;
    private Boolean pinned = false;
}