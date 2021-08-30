package com.example.zero.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id")
    private Long id;

    @Column(name = "usr_first_name")
    private String firstName;

    @Column(name = "usr_last_name")
    private String lastName;

    @Column(name = "usr_pwd")
    private String password;

    @Column(name = "usr_age")
    private short age;

    @Column(name = "usr_email")
    private String email;

    @Transient
    private String retypePassword;

    @Builder.Default
    @Enumerated
    @Column(name = "usr_role")
    private final UserRole userRole = UserRole.USER;

    @Builder.Default
    @Column(name = "usr_locked")
    private boolean locked = false;

    @Builder.Default
    @Column(name = "usr_enabled")
    private boolean enabled = false;

    @Builder.Default
    @Column(columnDefinition = "boolean default false", name = "usr_deleted", nullable = false)
    private boolean deleted = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}


