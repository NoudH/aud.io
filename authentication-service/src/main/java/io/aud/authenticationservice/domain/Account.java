package io.aud.authenticationservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    @ToString.Exclude
    private String password;

    @ElementCollection
    private List<String> claims;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @JsonIgnore
    private Integer lockoutCounter;

    public void addClaimsFromRole(Role role){
        claims.addAll(role.getClaims());
    }

    public void removeClaimsFromRole(Role role) {
        claims.removeAll(role.getClaims());
    }

    public void clearClaims(){
        claims.clear();
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public void incrementLockoutCounter(){
        this.lockoutCounter++;
    }
}
