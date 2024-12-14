package com.ecommerce.search.service.serviceimpl;

import com.ecommerce.search.model.dto.ProductDto;
import com.ecommerce.search.model.entity.ProductDocument;
import com.ecommerce.search.request.ProductSearchRepository;
import com.ecommerce.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ProductSearchRepository productSearchRepository;
    @Autowired
    private RestTemplate restTemplate;

    public List<ProductDocument> searchProducts(String name) {
        List<ProductDocument> products = productSearchRepository.findByNameContaining(name);
        products.sort(Comparator.comparing(this::calculateWeightedScore).reversed());
        return products;
    }

    public void indexProductInElasticsearch(ProductDto product) {

        String merchantNameUrl = "http://localhost:8082/api/users/merchantname/" + product.getMerchantId();
        String merchantName = restTemplate.getForObject(merchantNameUrl, String.class);


        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setPrice(product.getPrice());
        document.setStock(product.getStock());
        document.setFile(product.getFile());
        document.setMerchantId(product.getMerchantId());
        document.setMerchantName(merchantName);
        document.setProductRating(product.getRating());
        document.setMerchantTotalOrders(getTotalOrdersForMerchant(product.getMerchantId()));
        document.setRatingCount(product.getRatingCount());

        productSearchRepository.save(document);
    }


   public void  deleteAllProducts(){
        productSearchRepository.deleteAll();
   }
    private double calculateWeightedScore(ProductDocument product) {
        double ratingWeight = 0.4;
        double totalOrdersWeight = 0.35;
        double stockWeight = 0.15;
        double priceWeight = 0.1;

        double score = (product.getStock() * stockWeight) +
                (product.getProductRating() * ratingWeight) +
                (product.getPrice() * priceWeight) +
                (product.getMerchantTotalOrders() * totalOrdersWeight);
        return score;
    }

    public void incrementMerchantOrdersInElasticsearch(Long merchantId) {
        List<ProductDocument> merchantProducts = productSearchRepository.findAllByMerchantId(merchantId);

        for (ProductDocument product : merchantProducts) {
            product.setMerchantTotalOrders(product.getMerchantTotalOrders() + 1);
        }

        productSearchRepository.saveAll(merchantProducts);
    }

    public void deleteById(Long id){
        productSearchRepository.deleteById(id);
    }

    public int getTotalOrdersForMerchant(Long merchantId) {

        String url = "http://localhost:8082/api/order/count/{merchantId}";
        Map<String, Long> uriParams = new HashMap<>();
        uriParams.put("merchantId", merchantId);

        Integer totalOrders = restTemplate.getForObject(url, Integer.class, uriParams);

        return totalOrders != null ? totalOrders : 0;
    }
}
