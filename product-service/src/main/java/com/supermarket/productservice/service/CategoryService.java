package com.supermarket.productservice.service;

import com.supermarket.productservice.dto.CategoryDTO;
import com.supermarket.productservice.model.Category;
import com.supermarket.productservice.repository.CategoryRepository;
import com.supermarket.productservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Category not found"));
        return modelMapper.map(category, CategoryDTO.class);
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setId(null);
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null || !categoryRepository.existsById(categoryDTO.getId())) {
            throw new IllegalStateException("Category not found");
        }
        Category category = modelMapper.map(categoryDTO, Category.class);
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    public String deleteCategory(Long id) {
        if (productRepository.existsByCategoryId(id)) {
            throw new IllegalStateException("Category still has products and cannot be deleted");
        }
        categoryRepository.deleteById(id);
        return "Category Deleted Successfully";
    }

}
