package com.demo.product.service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),  new HashMap<>());
            return uploadResult.get("secure_url").toString(); // ✅ important
        } catch (Exception e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }
}