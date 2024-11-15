package com.farmmart.controller;

import com.farmmart.model.SubCategory;
import com.farmmart.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

/**
 * replaces: route/subCategory.route.js  +  controllers/subCategory.controller.js
 */
@RestController
@RequestMapping("/api/subcategory")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryRepository subCategoryRepo;

    // POST /api/subcategory/create
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SubCategory subCategory) {
        SubCategory saved = subCategoryRepo.save(subCategory);
        return ResponseEntity.ok(Map.of("message", "Sub Category Created",
            "data", saved, "error", false, "success", true));
    }

    // POST /api/subcategory/get
    @PostMapping("/get")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("data", subCategoryRepo.findAll(), "error", false, "success", true));
    }

    // PUT /api/subcategory/update
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("_id");
        if (id == null) return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));

        SubCategory sub = subCategoryRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SubCategory not found"));

        if (body.get("name")     != null) sub.setName((String) body.get("name"));
        if (body.get("image")    != null) sub.setImage((String) body.get("image"));
        if (body.get("category") != null) sub.setCategory((java.util.List<String>) body.get("category"));

        subCategoryRepo.save(sub);
        return ResponseEntity.ok(Map.of("message", "Updated successfully", "error", false, "success", true));
    }

    // DELETE /api/subcategory/delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        if (body.get("_id") == null) return ResponseEntity.badRequest().body(Map.of("message", "Provide _id"));
        subCategoryRepo.deleteById(body.get("_id"));
        return ResponseEntity.ok(Map.of("message", "Deleted successfully", "error", false, "success", true));
    }
}
