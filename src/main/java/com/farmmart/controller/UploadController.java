package com.farmmart.controller;

import com.farmmart.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * replaces: route/upload.router.js  +  controllers/uploadImage.controller.js
 * replaces: multer middleware (Spring Boot handles multipart automatically)
 *
 * The @RequestParam("image") MultipartFile file does exactly what multer did:
 * it receives the uploaded file from the request.
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class UploadController {

    private final CloudinaryService cloudinaryService;

    // POST /api/file/upload
    // replaces: router.post('/upload', upload.single('image'), uploadImageController)
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Please provide an image file", "error", true, "success", false));
        }

        String url = cloudinaryService.uploadImage(file);

        return ResponseEntity.ok(Map.of(
            "message", "Image uploaded",
            "data",    Map.of("url", url),
            "error",   false,
            "success", true
        ));
    }
}
