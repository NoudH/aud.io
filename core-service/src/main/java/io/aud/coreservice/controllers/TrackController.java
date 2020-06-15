package io.aud.coreservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.services.TrackService;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

@RestController
public class TrackController {

    private TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    //FIXME: change @RequestParam("track") to @RequestBody and !update frontend!
    @PostMapping("/")
    public Track postTrack(@ApiIgnore Authentication authentication, @RequestParam("file") MultipartFile file, @RequestParam("track") Track track) throws IOException, UnsupportedAudioFileException {
        return trackService.upload(track, file, authentication);
    }

    @GetMapping(value = "/files/{filename:.+}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename) throws IOException {
        /*Resource file = trackService.serveFile(filename);
        InputStream in = file.getInputStream();
        return IOUtils.toByteArray(in);*/

        Resource file = trackService.serveFile(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and @trackService.findById(#id).uploader.email == authentication.name")
    public void removeTrack(@PathVariable("id") Long id) {
        trackService.removeTrack(id);
    }

    @GetMapping("/search")
    public Page<Track> searchTrack(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return trackService.searchTracks(page, size, title);
    }

    @GetMapping("/artist/{id}")
    public Page<Track> getByArtist(@PathVariable("id") Long id, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "20") int size) {
        return trackService.findByArtist(id, page, size);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/uploader")
    public Page<Track> getOwnedTracks(
            @ApiIgnore Authentication authentication,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return trackService.findByUploaderSelf(authentication, page, size);
    }

    @GetMapping("/uploader/{id}")
    public Page<Track> getByUploader(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return trackService.findByUploader(id, page, size);
    }

    @Component
    public static class StringToTrackConverter implements Converter<String, Track> {

        private ObjectMapper objectMapper;

        public StringToTrackConverter(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        @SneakyThrows
        public Track convert(String source) {
            return objectMapper.readValue(source, Track.class);
        }
    }
}
