package io.aud.coreservice.controllers;

import io.aud.coreservice.domain.Track;
import io.aud.coreservice.services.TrackService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class TrackController {

    private TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/")
    public void postTrack(@ApiIgnore Authentication authentication, @RequestBody Track track) {
        trackService.save(track, authentication);
    }
}
