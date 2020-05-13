package io.aud.coreservice.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
public class Playlist {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @ManyToOne
    private UserAccount user;
    @Enumerated(EnumType.STRING)
    private Visibility visibility;
    @OneToMany
    private List<Track> tracks;

    public void addTrack(Track track) {
        if(tracks == null) {
            tracks = new ArrayList<>();
        }
        tracks.add(track);
    }

    public void removeTrack(Track track) {
        if(tracks == null) {
            return;
        }
        tracks.remove(track);
    }
}
