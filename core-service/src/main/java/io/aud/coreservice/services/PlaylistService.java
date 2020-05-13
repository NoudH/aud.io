package io.aud.coreservice.services;

import io.aud.coreservice.domain.Playlist;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.domain.Visibility;
import io.aud.coreservice.repositories.PlaylistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private UserAccountService userAccountService;
    private PlaylistRepository playlistRepository;
    private TrackService trackService;

    public PlaylistService(UserAccountService userAccountService, PlaylistRepository playlistRepository, TrackService trackService) {
        this.userAccountService = userAccountService;
        this.playlistRepository = playlistRepository;
        this.trackService = trackService;
    }

    public Playlist findById(Long id) {
        return playlistRepository.findById(id).get();
    }

    public Playlist create(Authentication authentication, Playlist playlist) {
        playlist.setUser(userAccountService.findByEmail(authentication.getName()));

        playlist.setTracks(trackService.findAllById(playlist.getTracks().stream().map(Track::getId).collect(Collectors.toList())));

        return playlistRepository.save(playlist);
    }

    public Page<Playlist> search(String name, int page, int size) {
        return playlistRepository.findAllByNameContainingAndVisibility(name, Visibility.PUBLIC, PageRequest.of(page, size));
    }

    public void deleteById(Long id) {
        playlistRepository.deleteById(id);
    }

    public Playlist changePlaylist(Long id, Playlist playlist) {
        Playlist entity = playlistRepository.findById(id).get();

        entity.setName(playlist.getName());
        entity.setVisibility(playlist.getVisibility());

        return playlistRepository.save(entity);
    }

    public Playlist addTrackToPlaylist(Long id, Long trackId) {
        Playlist entity = playlistRepository.findById(id).get();
        entity.addTrack(trackService.findById(trackId));

        return playlistRepository.save(entity);
    }

    public Playlist removeTrackFromPlaylist(Long id, Long trackId) {
        Playlist entity = playlistRepository.findById(id).get();
        entity.removeTrack(trackService.findById(trackId));

        return playlistRepository.save(entity);
    }
}
