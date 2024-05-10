package ru.practicum.admin.category;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.error.BadRequestException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.CategoryStorage;
import ru.practicum.storage.EventStorage;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminCategoryService {
    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;

    @Transactional
    public CategoryDto patchCategory(Long catId, CategoryDto categoryDto) {
        Category category = categoryStorage.findById(catId).orElseThrow(
                () -> new BadRequestException("Запрос составлен с ошибкой", "Такой категории нет."));
        category.setName(categoryDto.getName());
        categoryStorage.saveAndFlush(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        categoryStorage.save(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto deleteCategory(Long catId) {
        Optional<Category> category = categoryStorage.findById(catId);
        if (category.isEmpty()) {
            throw new BadRequestException("Запрос составлен с ошибкой", "Такой категории нет.");
        }
        if (!eventStorage.findCategoryByIdInEvent(catId).get().isEmpty()) {
            throw new DataIntegrityViolationException("Запрос составлен с ошибкой У категории есть events.");
        }
        categoryStorage.deleteById(category.get().getId());
        return CategoryMapper.toCategoryDto(category.get());
    }
}