package io.aud.coreservice.services;

import io.aud.coreservice.domain.Artist;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.repositories.ArtistRepository;
import io.aud.coreservice.repositories.TrackRepository;
import io.aud.coreservice.repositories.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrackService {

    private TrackRepository trackRepository;
    private UserAccountRepository userAccountRepository;
    private ArtistRepository artistRepository;

    public TrackService(TrackRepository trackRepository, UserAccountRepository userAccountRepository, ArtistRepository artistRepository) {
        this.trackRepository = trackRepository;
        this.userAccountRepository = userAccountRepository;
        this.artistRepository = artistRepository;
    }

    public Track upload(Track track, Authentication authentication) {
        track.setArtists((List<Artist>) artistRepository.findAllById(track.getArtists().stream().map(Artist::getId).collect(Collectors.toList())));

        if(authentication.isAuthenticated()){
            track.setUploader(userAccountRepository.findByEmail(authentication.getName()).get());
        } else {
            track.setUploader(null);
        }
        return trackRepository.save(track);
    }
}
