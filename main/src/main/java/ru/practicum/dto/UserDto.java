package ru.practicum.dto;


import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NonNull
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String name;
}