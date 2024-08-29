package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    /**
     * public
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Категория с id \"" + categoryId + "\" не найдена"));
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(PageRequest pageRequest) {
        List<Category> categories = categoryRepository.findAll(pageRequest).toList();
        return categories.stream().map(categoryMapper::categoryToCategoryDto).collect(Collectors.toList());
    }

    /**
     * admin
     */
    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.newCategoryDtoToCategory(newCategoryDto);
        Category newCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(newCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Категория с id \"" + categoryId + "\" не найдена"));
        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto newCategoryDTO) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException("Категория с id \"" + categoryId + "\" не найдена"));
        category.setName(newCategoryDTO.getName());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.categoryToCategoryDto(updatedCategory);
    }
}
