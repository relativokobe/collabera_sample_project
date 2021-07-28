package com.collaberaproducts.collaberaproducts.service;

import com.collaberaproducts.collaberaproducts.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    boolean addProduct(ProductDTO productDTO);
    ProductDTO getProductById(String id);
    List<ProductDTO> getAllProducts();
}

