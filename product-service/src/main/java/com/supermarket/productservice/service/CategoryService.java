package com.supermarket.productservice.service;

import com.supermarket.productservice.dto.CategoryDTO;
import com.supermarket.productservice.model.Category;
import com.supermarket.productservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return modelMapper.map(categories, new TypeToken<List<CategoryDTO>>() {}.getType());
    }

    public CategoryDTO getCategoryById(UUID id) {
        return modelMapper.map(categoryRepository.findById(id).orElse(null), CategoryDTO.class);
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        return modelMapper.map(categoryRepository.findBySlug(slug).orElse(null), CategoryDTO.class);
    }

    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        category.setId(null);
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);
    }

    public String deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
        return "Category Deleted Successfully";
    }

}
