package com.vahabvahabov.LoginDemo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import jakarta.validation.constraints.*;
import com.vahabvahabov.LoginDemo.model.OnComplete;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    @NotEmpty(message = "Username is required.", groups = OnComplete.class)
    @Size(min = 3, max = 40, message = "Username must be between 3 and 40 size.", groups = OnComplete.class)
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Password is required.", groups = OnComplete.class)
    @Pattern(regexp = "^(?=.*[0-9]).*$", message = "Password must contain at least one digit.", groups = OnComplete.class)
    private String password;

    @Email(message = "Please enter a valid mail.")
    @NotEmpty(message = "Mail is required.")
    // @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Only Gmail addresses are allowed.")
    private String mail;

    @Column(name = "date_of_birth")
    private Date date_of_birth;

    @Column(name = "verification_pin")
    private String verificationPin;

    @Column(name = "pin_expiration_time")
    private Date pinExpirationTime;

    @Column(name = "is_email_verified")
    private boolean isEmailVerified = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isEmailVerified;
    }
}