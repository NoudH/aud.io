package io.aud.coreservice.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class UserAccount {

    @Id
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

}
