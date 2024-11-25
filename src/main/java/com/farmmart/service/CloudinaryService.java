package com.farmmart.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

/**
 * replaces: utils/uploadImageClodinary.js
 *
 * uploadImage() takes a file from the request,
 * uploads it to Cloudinary, and returns the secure URL.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * replaces: uploadImageClodinary(image) from Node.js
     *
     * @param file - the uploaded file from MultipartFile (like req.file from multer)
     * @return the Cloudinary secure URL string
     */
    public String uploadImage(MultipartFile file) {
        try {
            // Convert MultipartFile to byte array and upload
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.emptyMap()
            );
            // Return the HTTPS URL of the uploaded image
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Image upload failed: " + e.getMessage()
            );
        }
    }
}
