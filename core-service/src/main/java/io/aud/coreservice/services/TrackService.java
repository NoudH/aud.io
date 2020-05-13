package io.aud.coreservice.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import io.aud.coreservice.domain.Artist;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.domain.Visibility;
import io.aud.coreservice.domain.dto.FileNameAwareByteArrayResource;
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
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrackService {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${s3.bucket.name}")
    private String bucketName;

    private final StorageService storageService;
    private TrackRepository trackRepository;
    private UserAccountRepository userAccountRepository;
    private ArtistRepository artistRepository;

    private AWSCredentials credentials = new BasicSessionCredentials(
            "ASIAVHAKLDQL73JH42RL",
            "59GgqraTJc7Qe0DM1bJ/ndoklvSvvcjPFSAxl5az",
            "FwoGZXIvYXdzECIaDIVqi0zMHJ21ZCruxiLLAa1SouLJKvJugvoRw+aHUy8uSzzV4ONfbTXCiKAZ21sReWcZG3ANFKtU/4FsvNMSjUgHyh4ki0vV7XD00xpr93QxKcU34UVygDP1iJLbumV/ZL4GV5yCNXbnbN0u66ZGaN/UXoSPwnQ3XgmRmL7rFOdfRi460cKd5RLwgYsaTJTQjnho/ufXX9sDBPat0hR0esJmfkQbYz/oIzWrnnv6mheS3HYUzWdmgCIDTGZKrxuPqzuz4xfLM6UBw/gqRJEzlenS7PlPQxGAxX0WKIDe7vUFMi0mBcYjz4xTSSc+ikH3JhrFhByoX77pw6ZpY5XVX3y2gUFXxI1HCKz2IZ1k8mA="
    );
    private AmazonS3 s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();

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
        s3client.putObject(bucketName, "tracks/" + name, convertMultiPartToFile(file, name));
        track.setAudioUrl("files/" + name);

        track.setDuration((int)ffprobe.probe("./files/" + name).getFormat().duration);

        if(authentication != null && authentication.isAuthenticated()){
            track.setUploader(userAccountRepository.findByEmail(authentication.getName()).get());
        } else {
            track.setUploader(null);
            if(track.getVisibility().equals(Visibility.PRIVATE)) {
                track.setVisibility(Visibility.HIDDEN);
            }
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

    public Resource serveFile(String filename) throws IOException {
        S3Object s3object = s3client.getObject(bucketName, "tracks/" + filename);
        S3ObjectInputStream inputStream = s3object.getObjectContent();

        return new FileNameAwareByteArrayResource(filename, StreamUtils.copyToByteArray(inputStream));
    }

    private File convertMultiPartToFile(MultipartFile file, String name) throws IOException {
        File convFile = new File("./files/" + name);
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public List<Track> findAllById(List<Long> ids) {
        return (List<Track>) trackRepository.findAllById(ids);
    }

    public Track findById(Long id) {
        return trackRepository.findById(id).get();
    }

    public void removeTrack(Long id) {
        Track entity = trackRepository.findById(id).get();

        s3client.deleteObject(bucketName, entity.getAudioUrl().replace("/files/", "tracks/"));

        entity.setVisibility(Visibility.REMOVED);
        entity.setDuration(0);
        entity.setArtists(null);
        entity.setName("[REMOVED] " + entity.getName());
        entity.setAudioUrl(null);
        entity.setUploader(null);

        trackRepository.save(entity);
    }
}
