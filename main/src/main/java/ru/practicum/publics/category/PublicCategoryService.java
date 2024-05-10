package ru.practicum.publics.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.storage.CategoryStorage;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PublicCategoryService {

    private final CategoryStorage categoryStorage;

    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from, size);
        List<Category> listCategory = categoryStorage.findAll(pageable).getContent();
        if (listCategory.isEmpty()) {
            throw new ObjectNotFoundException("Объект не найден. ", "There are no compilations");
        }
        return CategoryMapper.toListCategoryDto(listCategory);
    }

    public CategoryDto getCategory(Long catId) {
        Category category = categoryStorage.findById(catId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", String.format("Compilation with id=%d was not found.", catId)));
        return CategoryMapper.toCategoryDto(category);
    }
}