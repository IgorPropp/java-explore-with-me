package ru.practicum.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotBlank
    @Length(max = 50)
    private String name;
}