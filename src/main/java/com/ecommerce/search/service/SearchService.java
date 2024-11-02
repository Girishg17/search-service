package com.ecommerce.search.service;

import com.ecommerce.search.model.dto.ProductDto;
import com.ecommerce.search.model.entity.ProductDocument;

import java.util.List;

public interface SearchService {
    List<ProductDocument> searchProducts(String name);
    void indexProductInElasticsearch(ProductDto product);
    void incrementMerchantOrdersInElasticsearch(Long merchantId);
    void deleteById(Long id);
}
