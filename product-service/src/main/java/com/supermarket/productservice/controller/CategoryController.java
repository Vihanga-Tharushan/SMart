package com.supermarket.productservice.controller;

import com.supermarket.productservice.dto.CategoryDTO;
import com.supermarket.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getcategories")
    public List<CategoryDTO> getCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/category/{categoryId}")
    public CategoryDTO getCategoryById(@PathVariable UUID categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/category/slug/{slug}")
    public CategoryDTO getCategoryBySlug(@PathVariable String slug) {
        return categoryService.getCategoryBySlug(slug);
    }

    @PostMapping("/addcategory")
    public CategoryDTO saveCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.saveCategory(categoryDTO);
    }

    @PutMapping("/updatecategory")
    public CategoryDTO updateCategory(@RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryDTO);
    }

    @DeleteMapping("/deletecategory/{categoryId}")
    public String deleteCategory(@PathVariable UUID categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

}
