package com.supermarket.productservice.service;

import com.supermarket.productservice.dto.ProductDTO;
import com.supermarket.productservice.model.Product;
import com.supermarket.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
    }

    public ProductDTO getProductById(UUID id) {
        return modelMapper.map(productRepository.findById(id).orElse(null), ProductDTO.class);
    }

    public ProductDTO getProductBySku(String sku) {
        return modelMapper.map(productRepository.findBySku(sku).orElse(null), ProductDTO.class);
    }

    public ProductDTO getProductByBarcode(String barcode) {
        return modelMapper.map(productRepository.findByBarcode(barcode).orElse(null), ProductDTO.class);
    }

    public List<ProductDTO> getProductsByCategory(UUID categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
    }

    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        product.setId(null);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    public String deleteProduct(UUID id) {
        productRepository.deleteById(id);
        return "Product Deleted Successfully";
    }

}
