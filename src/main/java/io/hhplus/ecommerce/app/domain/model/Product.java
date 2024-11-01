package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "PRODUCT")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int price;

    private int sales;

    private LocalDateTime updateDate;


    public Product(long id, String name, int price, int sales) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sales = sales;
        this.updateDate = LocalDateTime.now();
    }

}
