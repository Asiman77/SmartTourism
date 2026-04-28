package com.ironhack.smarttourism.controller_tests;

import com.ironhack.smarttourism.controller.AdminCategoryController;
import com.ironhack.smarttourism.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminCategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private AdminCategoryController adminCategoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminCategoryController).build();
    }

    @Test
    void deleteCategory_shouldReturnSuccess() throws Exception {
        Long catId = 1L;
        doNothing().when(categoryService).deleteCategory(catId);

        mockMvc.perform(delete("/api/admin/categories/{id}", catId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted"));

        verify(categoryService, times(1)).deleteCategory(catId);
    }
}
