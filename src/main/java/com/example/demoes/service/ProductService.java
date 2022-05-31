package com.example.demoes.service;

import com.example.demoes.dto.ProductDto;
import com.example.demoes.entity.Product;
import com.example.demoes.repository.ProductRepository;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ElasticsearchOperations operations;

    public Product addProduct(ProductDto productDto) {
        return productRepository.save(new Product(productDto));
    }

    public Optional<Product> findProductById(String id) {
        return productRepository.findById(id);
    }

    public Page<Product> search(String brand, String category, String price, Integer minPrice, Integer maxPrice, int page, String sort) {
        Criteria criteria = new Criteria();
        if (!brand.equals(""))
//            criteria.and("brand").is(brand);
            criteria.subCriteria(new Criteria("brand").is(brand));
        if (!category.equals(""))
//            criteria.and("category").is(category);
            criteria.subCriteria(new Criteria("category").is(category));
        if (!price.equals("")) {
            if (price.equals("duoi-10"))
                criteria.subCriteria((new Criteria("price").lessThan(10_000_000)));
            else if (price.equals("tu-10-den-20"))
                criteria.subCriteria((new Criteria("price").between(10_000_000, 20_000_000)));
            else if (price.equals("tren-20"))
                criteria.subCriteria((new Criteria("price").greaterThan(20_000_000)));
        } else {
            if (minPrice != -1)
                criteria.subCriteria((new Criteria("price").greaterThanEqual(minPrice)));
            if (maxPrice != -1)
                criteria.subCriteria((new Criteria("price").lessThanEqual(maxPrice)));
        }

        Pageable pageable = null;
        if (!sort.equals("")) {
            pageable = sort.equals("asc") ? PageRequest.of(page - 1, 5, Sort.by("price").ascending()) :
                    PageRequest.of(page - 1, 5, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page - 1, 5);
        }

        Query query = new CriteriaQuery(criteria);
        query.setPageable(pageable);

        SearchHits<Product> productSearchHits = operations.search(query, Product.class);
//        List<Product> products = new ArrayList<>();
//        productSearchHits.forEach(x -> products.add(x.getContent()));
//        return products;
//        return productSearchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        SearchPage<Product> searchPage = SearchHitSupport.searchPageFor(productSearchHits, query.getPageable());
        return (Page)SearchHitSupport.unwrapSearchHits(searchPage);

    }

    public void addAll(List<Product> products) {
        productRepository.saveAll(products);
    }

    public List<Product> searchByKey(String key) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(key)
                        .field("name")
                        .field("brand")
                        .field("category")
                        .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                        .operator(Operator.AND))
                .build();

        SearchHits<Product> productSearchHits = operations.search(searchQuery, Product.class);
        //        List<Product> products = new ArrayList<>();
//        productSearchHits.forEach(x -> products.add(x.getContent()));
        return productSearchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    public Page<Product> searchByKey2(String key, int page, String sort) {
        Pageable pageable = null;
        if (!sort.equals("")) {
            pageable = sort.equals("asc") ? PageRequest.of(page - 1, 5, Sort.by("price").ascending()) :
                    PageRequest.of(page - 1, 5, Sort.by("price").descending());
        } else {
            pageable = PageRequest.of(page - 1, 5);
        }
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(key)
                        .field("name")
                        .field("brand")
                        .field("category")
                        .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                        .operator(Operator.AND)).withPageable(pageable)
                .build();

        SearchHits<Product> productSearchHits = operations.search(searchQuery, Product.class);

        //        List<Product> products = new ArrayList<>();
////        productSearchHits.forEach(x -> products.add(x.getContent()));
//        List<Product> products = productSearchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
//        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());
        SearchPage<Product> searchPage = SearchHitSupport.searchPageFor(productSearchHits, searchQuery.getPageable());
        return (Page)SearchHitSupport.unwrapSearchHits(searchPage);
    }

    public Page<Product> findAll(int page) {
        return productRepository.findAll(PageRequest.of(page - 1, 5));
    }
}
