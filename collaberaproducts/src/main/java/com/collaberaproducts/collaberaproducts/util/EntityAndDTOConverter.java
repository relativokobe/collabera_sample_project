package com.collaberaproducts.collaberaproducts.util;

import com.collaberaproducts.collaberaproducts.dto.ProductDTO;
import com.collaberaproducts.collaberaproducts.entity.Product;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityAndDTOConverter {

    public ProductDTO convertToDto(Product product) {
        return new ProductDTO(product.getId(), product.getName(), product.getDescription());
    }

    public Product convertToEntity(ProductDTO productDTO) throws ParseException {
        return new Product(productDTO.getId(), productDTO.getName(), productDTO.getDescription());
    }

    public List<ProductDTO> convertListOfProducts(List<Product> products) {
        List<ProductDTO> productDTOS = new ArrayList<>();
        for(Product product: products){
            productDTOS.add(new ProductDTO(product.getId(), product.getName(), product.getDescription()));
        }
        return productDTOS;
    }
}
