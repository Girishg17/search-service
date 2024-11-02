package com.ecommerce.search.controller;

import com.ecommerce.search.model.dto.ProductDto;
import com.ecommerce.search.model.entity.ProductDocument;
import com.ecommerce.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/products/search")
public class ProductSearchController {

    @Autowired
    private SearchService searchService;
    @PostMapping("/index")
    public ResponseEntity<Void> indexProduct(@RequestBody ProductDto product) {
        try {
            searchService.indexProductInElasticsearch(product);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/product")
    public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String name) {
        List<ProductDocument> products = searchService.searchProducts(name);
        return ResponseEntity.ok(products);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        // Perform deletion from Elasticsearch or any other related actions
        searchService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/increment/{merchantId}")
    public ResponseEntity<Void> incrementMerchantOrders(@PathVariable Long merchantId) {
        searchService.incrementMerchantOrdersInElasticsearch(merchantId);
        return ResponseEntity.ok().build();
    }
}
