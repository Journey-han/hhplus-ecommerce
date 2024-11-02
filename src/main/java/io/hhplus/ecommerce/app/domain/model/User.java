package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long userId;

    private String status;

    private LocalDate createDate;

    private LocalDate updateDate;

    public User(long userId, String status) {
        this.userId = userId;
        this.status = status;
        this.createDate = LocalDate.now();
    }
}
