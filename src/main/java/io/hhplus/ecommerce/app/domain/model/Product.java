package io.hhplus.ecommerce.app.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(name = "update_date")
    private String updateDate;


    public Product(long id, String name, int price, int sales, String updateDate) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sales = sales;
        this.updateDate = updateDate;
    }

    public Product(long id, int quantity) {
        this.id = id;
        this.sales+=quantity;
    }

    public void updateDate() {
        this.updateDate = String.valueOf(LocalDateTime.now());
    }
}
