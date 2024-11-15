package com.farmmart.controller;

import com.farmmart.model.Category;
import com.farmmart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * replaces: route/category.route.js  +  controllers/category.controller.js
 */
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepo;

    // POST /api/category/add-category
    @PostMapping("/add-category")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        if (category.getName() == null || category.getImage() == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide name and image", "error", true, "success", false));
        }
        Category saved = categoryRepo.save(category);
        return ResponseEntity.ok(Map.of("message", "Category Added", "data", saved,
            "error", false, "success", true));
    }

    // GET /api/category/get
    @GetMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("data", categoryRepo.findAll(), "error", false, "success", true));
    }

    // PUT /api/category/update
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, String> body) {
        String id = body.get("_id");
        if (id == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));
        }
        Category cat = categoryRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (body.get("name")  != null) cat.setName(body.get("name"));
        if (body.get("image") != null) cat.setImage(body.get("image"));
        categoryRepo.save(cat);
        return ResponseEntity.ok(Map.of("message", "Updated successfully", "error", false, "success", true));
    }

    // DELETE /api/category/delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        if (body.get("_id") == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));
        }
        categoryRepo.deleteById(body.get("_id"));
        return ResponseEntity.ok(Map.of("message", "Category deleted", "error", false, "success", true));
    }
}
