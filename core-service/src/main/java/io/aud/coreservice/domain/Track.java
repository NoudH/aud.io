package io.aud.coreservice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Track {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private String audioUrl;

    private Integer duration;

    @ManyToOne
    private UserAccount uploader;

    @ManyToMany
    private List<Artist> artists;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

}
