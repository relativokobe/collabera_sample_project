package com.collaberaproducts.collaberaproducts.serviceImpl;

import com.collaberaproducts.collaberaproducts.dto.LogDTO;
import com.collaberaproducts.collaberaproducts.dto.ProductDTO;
import com.collaberaproducts.collaberaproducts.entity.Product;
import com.collaberaproducts.collaberaproducts.repository.ProductRepository;
import com.collaberaproducts.collaberaproducts.service.ProductService;
import com.collaberaproducts.collaberaproducts.util.EntityAndDTOConverter;
import com.collaberaproducts.collaberaproducts.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityAndDTOConverter entityAndDTOConverter;

    @Autowired
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    private static final String TOPIC = "Logs";

    private static final int ID_COUNT = 10;

    @Override
    public boolean addProduct(final ProductDTO productDTO) {
        final IdGenerator idGenerator = new IdGenerator();
        boolean result = true;
        productDTO.setId(idGenerator.generate(ID_COUNT));
        try {
            this.productRepository.save(this.entityAndDTOConverter.convertToEntity(productDTO));
        } catch (ParseException e) {
            kafkaTemplate.send(TOPIC, new LogDTO(e.getMessage(), new Date(System.currentTimeMillis())));
            result = false;
        }
        kafkaTemplate.send(TOPIC, new LogDTO("Result of adding product: " + result, new Date(System.currentTimeMillis())));
        return result;
    }

    @Override
    public ProductDTO getProductById(final String id) {
        Product product = this.productRepository.findById(id).orElse(null);
        if(ObjectUtils.isEmpty(product)){
            kafkaTemplate.send(TOPIC, new LogDTO("Product not found using id = " + id, new Date(System.currentTimeMillis())));
            return null;
        }
        kafkaTemplate.send(TOPIC, new LogDTO("Product with id = "+ id + " is found", new Date(System.currentTimeMillis())));
        return this.entityAndDTOConverter.convertToDto(product);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        kafkaTemplate.send(TOPIC, new LogDTO("Returning all products", new Date(System.currentTimeMillis())));
        return this.entityAndDTOConverter.convertListOfProducts(products);
    }
}