package com.naevis.bot.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "time_entries")
public class TimeEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "session_id_seq")
    @SequenceGenerator(name = "session_id_seq", sequenceName = "session_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "duration_min")
    private Integer durationMin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @Column(name = "stopped_at")
    private Instant stoppedAt;

    @Column(name = "started_at", nullable = false, insertable = false)
    @Generated(GenerationTime.INSERT)
    private Instant startedAt;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Instant createdAt;
}
