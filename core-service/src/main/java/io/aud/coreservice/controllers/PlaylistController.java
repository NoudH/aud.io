package io.aud.coreservice.controllers;

import io.aud.coreservice.domain.Playlist;
import io.aud.coreservice.services.PlaylistService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public Playlist createNewPlaylist(@ApiIgnore Authentication authentication, @RequestBody Playlist playlist) {
        return playlistService.create(authentication, playlist);
    }

    @GetMapping("/{id}")
    @PostAuthorize("(returnObject.visibility != returnObject.visibility.PRIVATE and returnObject.visibility != returnObject.visibility.REMOVED) or returnObject.user.email == authentication.name")
    public Playlist getPlaylistById(@PathVariable("id") Long id) {
        return playlistService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @playlistService.findById(#id).user.email == authentication.name")
    public Playlist changePlaylist(@PathVariable("id") Long id, @RequestBody Playlist playlist) {
        return playlistService.changePlaylist(id, playlist);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @playlistService.findById(#id).user.email == authentication.name")
    public void deletePlaylist(@PathVariable("id") Long id) {
        playlistService.deleteById(id);
    }

    @PostMapping("/{id}/tracks/{trackId}")
    @PreAuthorize("isAuthenticated() and @playlistService.findById(#id).user.email == authentication.name")
    public Playlist addTrackToPlaylist(@PathVariable("id") Long id, @PathVariable("trackId") Long trackId) {
        return playlistService.addTrackToPlaylist(id, trackId);
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    @PreAuthorize("isAuthenticated() and @playlistService.findById(#id).user.email == authentication.name")
    public Playlist removeTrackFromPlaylist(@PathVariable("id") Long id, @PathVariable("trackId") Long trackId) {
        return playlistService.removeTrackFromPlaylist(id, trackId);
    }

    @GetMapping("/search")
    public Page<Playlist> searchPlaylist(
            @RequestParam("name") String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return playlistService.search(name, page, size);
    }
}
