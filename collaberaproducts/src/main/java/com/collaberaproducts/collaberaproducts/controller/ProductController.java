package com.collaberaproducts.collaberaproducts.controller;

import com.collaberaproducts.collaberaproducts.dto.LogDTO;
import com.collaberaproducts.collaberaproducts.dto.ProductDTO;
import com.collaberaproducts.collaberaproducts.exceptions.ParameterIsNullException;
import com.collaberaproducts.collaberaproducts.service.ProductService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    private static final String TOPIC = "Logs";

    @PostMapping("/add")
    public void addProduct(final @RequestBody ProductDTO productDTO, final HttpServletRequest request, final HttpServletResponse response) throws ParameterIsNullException {
        final String url = request.getRequestURI();
        if(productDTO == null){
            kafkaTemplate.send(TOPIC, new LogDTO(url + "Product is null", new Date(System.currentTimeMillis())));
            throw new ParameterIsNullException("Request Parameter (Product) is null");
        }
        int responseStatus = HttpServletResponse.SC_OK;
        if(!this.productService.addProduct(productDTO)){
            responseStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        kafkaTemplate.send(TOPIC, new LogDTO(url + " Adding of products response status = " + responseStatus, new Date(System.currentTimeMillis())));
        response.setStatus(responseStatus);
    }

    @GetMapping("/{id}")
    public void getProductUsingId(final @PathVariable String id, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ParameterIsNullException {
        final String url = request.getRequestURI();
        if(StringUtils.isEmpty(id)){
            kafkaTemplate.send(TOPIC, new LogDTO(url + " id is null or empty", new Date(System.currentTimeMillis())));
            throw new ParameterIsNullException("Request Parameter (id) is null");
        }

        int responseStatus = HttpServletResponse.SC_NOT_FOUND;
        final ProductDTO productDTO = this.productService.getProductById(id);
        if(productDTO != null){
            responseStatus = HttpServletResponse.SC_OK;
            final Gson gson = new GsonBuilder().serializeNulls().create();
            final String stringResponse = gson.toJson(productDTO);
            response.getWriter().write(stringResponse);
        }
        kafkaTemplate.send(TOPIC, new LogDTO(url + " Retrieving product using id response status = " + responseStatus, new Date(System.currentTimeMillis())));
        response.setStatus(responseStatus);
    }

    @GetMapping("/getAll")
    public void getAllProducts(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Gson gson = new GsonBuilder().serializeNulls().create();
        final String stringResponse;
        List<ProductDTO> products = this.productService.getAllProducts();
        stringResponse = gson.toJson(products);
        response.getWriter().write(stringResponse);
        kafkaTemplate.send(TOPIC, new LogDTO(request.getRequestURI() + " Number of products retrieved = " + products.size()  , new Date(System.currentTimeMillis())));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
