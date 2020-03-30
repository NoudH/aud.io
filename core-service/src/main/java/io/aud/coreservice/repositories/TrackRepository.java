package io.aud.coreservice.repositories;

import io.aud.coreservice.domain.Artist;
import io.aud.coreservice.domain.Track;
import io.aud.coreservice.domain.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TrackRepository extends PagingAndSortingRepository<Track, Long> {

    Page<Track> findAllByNameContainingIgnoreCaseAndVisibility(String name, Visibility visibility, Pageable pageable);

    Page<Track> findAllByArtistsAndVisibility(Artist artist, Visibility visibility, Pageable pageable);

    Page<Track> findAllByUploader_Email(String uploader_email, Pageable pageable);

    Page<Track> findAllByUploader_IdAndVisibility(Long uploader_id, Visibility visibility, Pageable pageable);
}
