package com.ecommerce.search.request;

import com.ecommerce.search.model.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
    List<ProductDocument> findByNameContaining(String name);
    List<ProductDocument> findAllByMerchantId(Long merchantId);
}
