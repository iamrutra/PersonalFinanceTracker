package pl.arturchub.PersonalFinanceTracker.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.arturchub.PersonalFinanceTracker.models.Category;
import pl.arturchub.PersonalFinanceTracker.repositories.CategoryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
