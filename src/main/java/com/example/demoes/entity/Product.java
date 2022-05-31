package com.example.demoes.entity;

import com.example.demoes.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Document(indexName = "tgdd-index")
@Document(indexName = "test-index-3")
public class Product {
    @Id
    private String id;
    private String name;
    private Double price;
    private String brand;
    private String category;

    public Product(ProductDto productDto) {
        this.id = productDto.getId();
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.brand = productDto.getBrand();
        this.category = productDto.getCategory();
    }
}
