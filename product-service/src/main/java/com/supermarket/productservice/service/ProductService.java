package com.supermarket.productservice.service;

import com.supermarket.productservice.dto.ProductDTO;
import com.supermarket.productservice.model.Product;
import com.supermarket.productservice.repository.CategoryRepository;
import com.supermarket.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
        return modelMapper.map(product, ProductDTO.class);
    }

    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return modelMapper.map(products, new TypeToken<List<ProductDTO>>() {}.getType());
    }

    public ProductDTO saveProduct(ProductDTO productDTO) {
        validate(productDTO);
        Product product = modelMapper.map(productDTO, Product.class);
        product.setId(null);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    public ProductDTO updateProduct(ProductDTO productDTO) {
        if (productDTO.getId() == null || !productRepository.existsById(productDTO.getId())) {
            throw new IllegalStateException("Product not found");
        }
        validate(productDTO);
        Product product = modelMapper.map(productDTO, Product.class);
        return modelMapper.map(productRepository.save(product), ProductDTO.class);
    }

    public String deleteProduct(Long id) {
        productRepository.deleteById(id);
        return "Product Deleted Successfully";
    }

    private void validate(ProductDTO productDTO) {
        if (productDTO.getCategoryId() == null || !categoryRepository.existsById(productDTO.getCategoryId())) {
            throw new IllegalStateException("Category not found");
        }
        if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Price must be zero or greater");
        }
    }

}
