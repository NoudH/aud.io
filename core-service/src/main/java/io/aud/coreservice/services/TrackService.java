package io.aud.coreservice.services;

import io.aud.coreservice.domain.Artist;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.domain.Visibility;
import io.aud.coreservice.repositories.ArtistRepository;
import io.aud.coreservice.repositories.TrackRepository;
import io.aud.coreservice.repositories.UserAccountRepository;
import net.bramp.ffmpeg.FFprobe;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrackService {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    private final StorageService storageService;
    private TrackRepository trackRepository;
    private UserAccountRepository userAccountRepository;
    private ArtistRepository artistRepository;

    public TrackService(StorageService storageService, TrackRepository trackRepository, UserAccountRepository userAccountRepository, ArtistRepository artistRepository) {
        this.storageService = storageService;
        this.trackRepository = trackRepository;
        this.userAccountRepository = userAccountRepository;
        this.artistRepository = artistRepository;
    }

    public Track upload(Track track, MultipartFile file, Authentication authentication) throws IOException, UnsupportedAudioFileException {
        FFprobe ffprobe = new FFprobe(ffmpegPath);

        if(track.getArtists() == null) {
            track.setArtists(new ArrayList<>());
        }
        track.setArtists((List<Artist>) artistRepository.findAllById(track.getArtists().stream().map(Artist::getId).collect(Collectors.toList())));

        String name = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        storageService.store(file, name);
        track.setAudioUrl(name);

        track.setDuration((int)ffprobe.probe("./files/" + name).getFormat().duration);

        if(authentication != null && authentication.isAuthenticated()){
            track.setUploader(userAccountRepository.findByEmail(authentication.getName()).get());
        } else {
            track.setUploader(null);
        }
        return trackRepository.save(track);
    }

    public Page<Track> searchTracks(int page, int size, String title) {
        Pageable pageable = PageRequest.of(page, size);
        return trackRepository.findAllByNameContainingIgnoreCaseAndVisibility(title, Visibility.PUBLIC, pageable);
    }

    public Page<Track> findByArtist(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trackRepository.findAllByArtistsAndVisibility(artistRepository.findById(id).get(), Visibility.PUBLIC, pageable);
    }

    public Page<Track> findByUploaderSelf(Authentication authentication, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trackRepository.findAllByUploader_Email(authentication.getName(), pageable);
    }

    public Page<Track> findByUploader(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return trackRepository.findAllByUploader_IdAndVisibility(id, Visibility.PUBLIC, pageable);
    }

    public Resource serveFile(String filename) {
        return storageService.loadAsResource(filename);
    }
}
