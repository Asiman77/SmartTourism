package com.ironhack.smarttourism.controller;

import com.ironhack.smarttourism.common.response.ApiResponse;
import com.ironhack.smarttourism.dto.request.CategoryRequestDTO;
import com.ironhack.smarttourism.dto.response.CategoryResponseDTO;
import com.ironhack.smarttourism.mapper.CategoryMapper;
import com.ironhack.smarttourism.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponseDTO>> getAllCategories() {

        var categories = categoryService.getAllCategories()
                .stream()
                .map(CategoryMapper::toResponseDTO)
                .toList();

        return new ApiResponse<>(true, "Categories fetched", categories);
    }

    @PostMapping
    public ApiResponse<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO dto) {
        var category = categoryService.createCategory(dto);
        return new ApiResponse<>(true, "Category created",
                CategoryMapper.toResponseDTO(category));
    }

    @PatchMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> updateCategory(@PathVariable Long id,
                                                           @RequestBody CategoryRequestDTO dto) {

        var category = categoryService.updateCategory(id, dto);
        return new ApiResponse<>(true, "Category updated",
                CategoryMapper.toResponseDTO(category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return new ApiResponse<>(true, "Category deleted", null);
    }
}