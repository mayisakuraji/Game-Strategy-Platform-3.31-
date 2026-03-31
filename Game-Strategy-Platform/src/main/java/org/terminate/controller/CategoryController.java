package org.terminate.controller;

import org.terminate.common.Result;
import org.terminate.entity.Category;
import org.terminate.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin // 解决跨域，方便 Vue 调用
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryRepository.findAll());
    }
}