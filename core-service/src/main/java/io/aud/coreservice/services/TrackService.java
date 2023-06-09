package io.aud.coreservice.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TrackService {

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${ffmpeg.path}")
    private String ffmpegUrl;

    @Value("${aws.accesskey}")
    private String awsAccessKey;

    @Value("${aws.secretkey}")
    private String awsSecretKey;

    @Value("${aws.sessiontoken}")
    private String awsSessionToken;

    @Value("${s3.bucket.name}")
    private String bucketName;

    private final StorageService storageService;
    private TrackRepository trackRepository;
    private UserAccountRepository userAccountRepository;
    private ArtistRepository artistRepository;

    private AWSCredentials credentials;
    private AmazonS3 s3client;

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
        s3client.putObject(new PutObjectRequest(bucketName, "tracks/" + name, convertMultiPartToFile(file, name)).withCannedAcl(CannedAccessControlList.PublicRead));
        track.setAudioUrl(name);

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

    @PostConstruct
    private void setupFFmpeg() throws URISyntaxException, IOException {
        if(!SystemUtils.IS_OS_WINDOWS) {
            String[] command = {"/bin/sh", "-c", "cp ./ffprobe /tmp/ffprobe && chmod 755 /tmp/ffmpeg"};
            Runtime.getRuntime().exec(command);
            System.out.println("FFMPEG successfully installed!");
        }
    }

    @PostConstruct
    private void setupAwsCreds() {
        credentials = new BasicSessionCredentials(
                awsAccessKey,
                awsSecretKey,
                awsSessionToken
        );

        s3client = AmazonS3ClientBuilder
            .standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();
    }
}
