package com.collaberaproducts.collaberaproducts.controller;

import com.collaberaproducts.collaberaproducts.dto.LogDTO;
import com.collaberaproducts.collaberaproducts.dto.ProductDTO;
import com.collaberaproducts.collaberaproducts.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    private static final String TOPIC = "Logs";

    @PostMapping("/products/add")
    public void addProduct(final @RequestBody ProductDTO productDTO, final HttpServletResponse response){
        if(productDTO == null){
            kafkaTemplate.send(TOPIC, new LogDTO("Product is null", new Date(System.currentTimeMillis())));
            //TODO THROW EXCEPTION
            return;
        }
        int responseStatus = HttpServletResponse.SC_OK;
        if(!this.productService.addProduct(productDTO)){
            responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        kafkaTemplate.send(TOPIC, new LogDTO("Adding of products response status = " + responseStatus, new Date(System.currentTimeMillis())));
        response.setStatus(responseStatus);
    }

    @GetMapping("/products/{id}")
    public void getProductUsingId(final @PathVariable String id, final HttpServletResponse response) throws IOException {
        if(StringUtils.isEmpty(id)){
            kafkaTemplate.send(TOPIC, new LogDTO("Product is null", new Date(System.currentTimeMillis())));
            //TODO THROW EXCEPTION
            return;
        }

        int responseStatus = HttpServletResponse.SC_NOT_FOUND;
        final ProductDTO productDTO = this.productService.getProductById(id);
        if(productDTO != null){
            responseStatus = HttpServletResponse.SC_OK;
            final Gson gson = new GsonBuilder().serializeNulls().create();
            final String stringResponse = gson.toJson(productDTO);
            response.getWriter().write(stringResponse);
        }
        kafkaTemplate.send(TOPIC, new LogDTO("Retrieving product using id response status = " + responseStatus, new Date(System.currentTimeMillis())));
        response.setStatus(responseStatus);
    }

    @GetMapping("/products")
    public void getAllProducts(final HttpServletResponse response) throws IOException {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        final String stringResponse;
        List<ProductDTO> products = this.productService.getAllProducts();
        stringResponse = gson.toJson(products);
        response.getWriter().write(stringResponse);
        kafkaTemplate.send(TOPIC, new LogDTO("Number of products retrieved = " + products.size()  , new Date(System.currentTimeMillis())));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
