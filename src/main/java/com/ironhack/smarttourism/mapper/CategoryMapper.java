package com.ironhack.smarttourism.mapper;

import com.ironhack.smarttourism.dto.request.CategoryRequestDTO;
import com.ironhack.smarttourism.dto.response.CategoryResponseDTO;
import com.ironhack.smarttourism.entity.Category;

public class CategoryMapper {

    public static Category toEntity(CategoryRequestDTO dto) {
        if (dto == null) return null;

        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public static CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) return null;

        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
