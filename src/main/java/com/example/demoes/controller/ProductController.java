package com.example.demoes.controller;

import com.example.demoes.dto.ProductDto;
import com.example.demoes.entity.Product;
import com.example.demoes.service.ProductService;
import com.example.demoes.util.DataCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    ProductService productService;

    @Autowired
    DataCrawler dataCrawler;

    @PostMapping()
    ResponseEntity<?> addProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.addProduct(productDto));
    }

    @GetMapping("{id}")
    ResponseEntity<?> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.findProductById(id));
    }

    @GetMapping()
    ResponseEntity<?> search(
            @RequestParam(name = "brand", required = false, defaultValue = "") String brand,
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "price", required = false, defaultValue = "") String price,
            @RequestParam(name = "minPrice", required = false, defaultValue = "-1") Integer minPrice,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "-1") Integer maxPrice,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "") String sort
            ) {
        return ResponseEntity.ok(productService.search(brand, category, price, minPrice, maxPrice, page, sort));
    }

    @GetMapping("/search")
    ResponseEntity<?> searchByKey(
            @RequestParam(name = "key", required = false, defaultValue = "") String key
            ) {
        return ResponseEntity.ok(productService.searchByKey(key));
    }

    @GetMapping("/search2")
    ResponseEntity<?> searchByKey2(
            @RequestParam(name = "key", required = false, defaultValue = "") String key,
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(name = "sort", required = false, defaultValue = "") String sort

            ) {
        return ResponseEntity.ok(productService.searchByKey2(key, page, sort));
    }

    @GetMapping("addAll")
    ResponseEntity<?> addAllProduct() {
        List<Product> data = dataCrawler.getData();
        productService.addAll(data);
        return ResponseEntity.ok(String.format("Them %s san pham thanh cong", data.size()));
    }

    @GetMapping("page/{i}")
    ResponseEntity<?> findAll(@PathVariable int i) {
        return ResponseEntity.ok(productService.findAll(i));
    }
}
