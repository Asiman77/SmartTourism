package com.ironhack.smarttourism.service_tests;

import com.ironhack.smarttourism.dto.request.CategoryRequestDTO;
import com.ironhack.smarttourism.entity.Category;
import com.ironhack.smarttourism.repository.CategoryRepository;
import com.ironhack.smarttourism.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category sampleCategory;

    private final Long categoryId = 1L;

    @BeforeEach
    void setUp() {
        sampleCategory = new Category();
        sampleCategory.setId(categoryId);
        sampleCategory.setName("Adventure");
        sampleCategory.setDescription("Adventure tours");
    }

    @Test
    void getAllCategories_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(sampleCategory));

        List<Category> result = categoryService.getAllCategories();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Adventure", result.get(0).getName());

        verify(categoryRepository).findAll();
    }

    @Test
    void createCategory_ShouldSaveCategory() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Culture");
        dto.setDescription("Cultural tours");

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.createCategory(dto);

        assertNotNull(result);
        assertEquals("Culture", result.getName());
        assertEquals("Cultural tours", result.getDescription());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_ShouldUpdateFieldsCorrectly() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Updated Category");
        dto.setDescription("Updated Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategory(categoryId, dto);

        assertEquals("Updated Category", result.getName());
        assertEquals("Updated Description", result.getDescription());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(sampleCategory);
    }

    @Test
    void updateCategory_ShouldUpdateOnlyName_WhenDescriptionIsNull() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Only Name Updated");
        dto.setDescription(null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.updateCategory(categoryId, dto);

        assertEquals("Only Name Updated", result.getName());
        assertEquals("Adventure tours", result.getDescription());

        verify(categoryRepository).save(sampleCategory);
    }

    @Test
    void updateCategory_ShouldThrowException_WhenCategoryNotFound() {
        CategoryRequestDTO dto = new CategoryRequestDTO();
        dto.setName("Updated Category");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.updateCategory(categoryId, dto)
        );

        assertEquals("Category not found", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(sampleCategory));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).delete(sampleCategory);
    }

    @Test
    void deleteCategory_ShouldThrowException_WhenCategoryNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> categoryService.deleteCategory(categoryId)
        );

        assertEquals("Category not found", exception.getMessage());

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}