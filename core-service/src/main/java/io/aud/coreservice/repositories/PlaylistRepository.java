package io.aud.coreservice.repositories;

import io.aud.coreservice.domain.Playlist;
import io.aud.coreservice.domain.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlaylistRepository extends PagingAndSortingRepository<Playlist, Long> {

    Page<Playlist> findAllByNameContainingAndVisibility(String name, Visibility visibility, Pageable pageable);
}
