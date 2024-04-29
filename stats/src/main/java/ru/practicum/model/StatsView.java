package ru.practicum.model;

import lombok.*;

@Data
@AllArgsConstructor
public class StatsView {

    private String app;
    private String uri;
    private Long hits;
}