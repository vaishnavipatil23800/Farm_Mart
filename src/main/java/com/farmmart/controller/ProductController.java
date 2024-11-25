package com.farmmart.controller;

import com.farmmart.model.Product;
import com.farmmart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * replaces: route/product.route.js  +  controllers/product.controller.js
 *
 * All URLs are identical to your Node.js routes.
 * The "typo" in the URL get-pruduct-by-category-and-subcategory is kept
 * intentionally so your frontend SummaryApi.js doesn't need changes.
 */
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepo;

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/create
    // replaces: createProductController
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Product product) {
        if (product.getName() == null || product.getImage().isEmpty()
            || product.getCategory().isEmpty() || product.getSubCategory().isEmpty()
            || product.getUnit() == null || product.getPrice() == null
            || product.getDescription() == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Enter required fields", "error", true, "success", false));
        }
        Product saved = productRepo.save(product);
        return ResponseEntity.ok(Map.of("message", "Product Created Successfully",
            "data", saved, "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/get
    // replaces: getProductController (with pagination + search)
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/get")
    public ResponseEntity<?> getAll(@RequestBody(required = false) Map<String, Object> body) {
        int page   = (body != null && body.get("page")  != null) ? (int) body.get("page")  : 1;
        int limit  = (body != null && body.get("limit") != null) ? (int) body.get("limit") : 10;
        String search = (body != null) ? (String) body.get("search") : null;

        Pageable pageable = PageRequest.of(page - 1, limit,
            Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> result;
        if (search != null && !search.isBlank()) {
            result = productRepo.findByTextSearch(search, pageable);
        } else {
            result = productRepo.findAll(pageable);
        }

        return ResponseEntity.ok(Map.of(
            "message",     "Product data",
            "error",       false,
            "success",     true,
            "totalCount",  result.getTotalElements(),
            "totalNoPage", result.getTotalPages(),
            "data",        result.getContent()
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/get-product-by-category
    // replaces: getProductByCategory
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/get-product-by-category")
    public ResponseEntity<?> getByCategory(@RequestBody Map<String, String> body) {
        if (body.get("id") == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "provide category id", "error", true, "success", false));
        }
        Pageable pageable = PageRequest.of(0, 15);
        List<Product> products = productRepo.findByCategoryIn(List.of(body.get("id")), pageable);
        return ResponseEntity.ok(Map.of("message", "category product list",
            "data", products, "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/get-pruduct-by-category-and-subcategory
    // NOTE: "pruduct" typo is intentional — kept to match your frontend SummaryApi.js
    // replaces: getProductByCategoryAndSubCategory
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/get-pruduct-by-category-and-subcategory")
    public ResponseEntity<?> getByCategoryAndSubCategory(@RequestBody Map<String, Object> body) {
        String categoryId    = (String) body.get("categoryId");
        String subCategoryId = (String) body.get("subCategoryId");
        int page  = body.get("page")  != null ? (int) body.get("page")  : 1;
        int limit = body.get("limit") != null ? (int) body.get("limit") : 10;

        if (categoryId == null || subCategoryId == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Provide categoryId and subCategoryId"));
        }

        Pageable pageable = PageRequest.of(page - 1, limit,
            Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> result = productRepo.findByCategoryInAndSubCategoryIn(
            List.of(categoryId), List.of(subCategoryId), pageable);

        return ResponseEntity.ok(Map.of(
            "message",    "Product list",
            "data",       result.getContent(),
            "totalCount", result.getTotalElements(),
            "page",       page,
            "limit",      limit,
            "success",    true,
            "error",      false
        ));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/get-product-details
    // replaces: getProductDetails
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/get-product-details")
    public ResponseEntity<?> getDetails(@RequestBody Map<String, String> body) {
        Product product = productRepo.findById(body.get("productId"))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return ResponseEntity.ok(Map.of("message", "product details",
            "data", product, "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/product/update-product-details
    // replaces: updateProductDetails
    // SECURITY FIX: No longer uses ...request.body spread — only explicit fields
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/update-product-details")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        String id = (String) body.get("_id");
        if (id == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "provide product _id", "error", true, "success", false));
        }

        Product product = productRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Only update fields that are explicitly sent — no injection possible
        if (body.get("name")        != null) product.setName((String) body.get("name"));
        if (body.get("description") != null) product.setDescription((String) body.get("description"));
        if (body.get("unit")        != null) product.setUnit((String) body.get("unit"));
        if (body.get("publish")     != null) product.setPublish((Boolean) body.get("publish"));
        if (body.get("price")       != null) product.setPrice(Double.parseDouble(body.get("price").toString()));
        if (body.get("discount")    != null) product.setDiscount(Double.parseDouble(body.get("discount").toString()));
        if (body.get("stock")       != null) product.setStock(Integer.parseInt(body.get("stock").toString()));
        if (body.get("image")       != null) product.setImage((List<String>) body.get("image"));
        if (body.get("category")    != null) product.setCategory((List<String>) body.get("category"));
        if (body.get("subCategory") != null) product.setSubCategory((List<String>) body.get("subCategory"));

        productRepo.save(product);
        return ResponseEntity.ok(Map.of("message", "updated successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/product/delete-product
    // replaces: deleteProductDetails
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/delete-product")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        if (body.get("_id") == null) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "provide _id", "error", true, "success", false));
        }
        productRepo.deleteById(body.get("_id"));
        return ResponseEntity.ok(Map.of("message", "Delete successfully", "error", false, "success", true));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/product/search-product
    // replaces: searchProduct
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/search-product")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> body) {
        String search = (String) body.get("search");
        int page  = body.get("page")  != null ? (int) body.get("page")  : 1;
        int limit = body.get("limit") != null ? (int) body.get("limit") : 10;

        Pageable pageable = PageRequest.of(page - 1, limit,
            Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Product> result = (search != null && !search.isBlank())
            ? productRepo.findByTextSearch(search, pageable)
            : productRepo.findAll(pageable);

        return ResponseEntity.ok(Map.of(
            "message",    "Product data",
            "error",      false,
            "success",    true,
            "data",       result.getContent(),
            "totalCount", result.getTotalElements(),
            "totalPage",  result.getTotalPages(),
            "page",       page,
            "limit",      limit
        ));
    }
}
