package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "location_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long lat;
    @Column(nullable = false)
    private Long lon;
    @Column(nullable = false)
    private Long rad;
    @ManyToMany
    @JoinTable(name = "location_groups_events", joinColumns = @JoinColumn(name = "location_event_group_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> eventGroup = new HashSet<>();
}