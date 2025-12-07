package com.onlinestore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    private final Path uploadsRoot;
    private final Path productDir;

    public ImageStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadsRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.productDir = uploadsRoot.resolve("products");
        try {
            Files.createDirectories(productDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize upload directory", e);
        }
    }

    public String storeProductImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("Image file must have a name");
        }

        String cleanFilename = StringUtils.cleanPath(Objects.requireNonNull(originalFilename));
        String extension = StringUtils.getFilenameExtension(cleanFilename);

        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Image file must have an extension");
        }

        extension = extension.toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Only .jpg, .jpeg or .png images are allowed");
        }

        String newFilename = UUID.randomUUID() + "." + extension;
        Path destination = productDir.resolve(newFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store image", e);
        }

        return "products/" + newFilename;
    }

    public void deleteImage(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        Path imagePath = uploadsRoot.resolve(relativePath).normalize();
        if (!imagePath.startsWith(uploadsRoot)) {
            return;
        }

        try {
            Files.deleteIfExists(imagePath);
        } catch (IOException ignored) {
            // silently ignore delete issues to avoid blocking user flow
        }
    }
}


