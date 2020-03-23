package io.aud.coreservice.repositories;

import io.aud.coreservice.domain.Track;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TrackRepository extends PagingAndSortingRepository<Track, Long> {

}
