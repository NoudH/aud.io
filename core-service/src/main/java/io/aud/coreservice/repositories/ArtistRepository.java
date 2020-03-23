package io.aud.coreservice.repositories;

import io.aud.coreservice.domain.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ArtistRepository extends PagingAndSortingRepository<Artist, Long> {
}
