package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long userId;

    private String status;

    private LocalDate createDate;

    private LocalDate updateDate;

}
