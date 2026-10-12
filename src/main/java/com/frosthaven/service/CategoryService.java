package com.frosthaven.service;

import com.frosthaven.entity.Category;
import com.frosthaven.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Integer id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Category not found"));
    }

    public Category addCategory(Category category) {

        if (category.getCategoryName() == null ||
                category.getCategoryName().isBlank()) {

            throw new RuntimeException(
                    "Category name is required");
        }

        if (categoryRepository.existsByCategoryName(
                category.getCategoryName())) {

            throw new RuntimeException(
                    "Category already exists");
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(
            Integer id,
            Category updatedCategory) {

        Category category = getCategory(id);

        if (updatedCategory.getCategoryName() == null ||
                updatedCategory.getCategoryName().isBlank()) {

            throw new RuntimeException(
                    "Category name is required");
        }

        category.setCategoryName(
                updatedCategory.getCategoryName());

        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id) {

        if (!categoryRepository.existsById(id)) {

            throw new RuntimeException(
                    "Category not found");
        }

        categoryRepository.deleteById(id);
    }
}