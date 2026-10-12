
package com.frosthaven.controller;

import com.frosthaven.entity.Product;
import com.frosthaven.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    private static final Path IMAGE_DIRECTORY =
            Paths.get("src/main/resources/static/images")
                    .toAbsolutePath()
                    .normalize();

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(
            @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(
                    productService.getProduct(id)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Upload an image and return its filename.
    @PostMapping(
            value = "/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile image) {

        try {
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select an image."));
            }

            if (image.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Image must be smaller than 5 MB."));
            }

            String contentType = image.getContentType();

            if (contentType == null ||
                    !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "Only JPG, PNG, and WebP images are allowed."));
            }

            String extension;

            switch (contentType.toLowerCase()) {
                case "image/jpeg":
                    extension = ".jpg";
                    break;
                case "image/png":
                    extension = ".png";
                    break;
                case "image/webp":
                    extension = ".webp";
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Unsupported image type."));
            }

            // Check the actual file signature, not only the browser MIME type.
            byte[] bytes = image.getBytes();

            if (!isValidImage(bytes, contentType.toLowerCase())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error",
                                "The selected file is not a valid image."));
            }

            Files.createDirectories(IMAGE_DIRECTORY);

            String filename = UUID.randomUUID() + extension;
            Path destination = IMAGE_DIRECTORY.resolve(filename)
                    .normalize();

            if (!destination.startsWith(IMAGE_DIRECTORY)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid image filename."));
            }

            Files.write(destination, bytes);

            return ResponseEntity.ok(
                    Map.of(
                            "message", "Image uploaded successfully.",
                            "image", filename,
                            "imageUrl", "/images/" + filename
                    )
            );

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error",
                            "Could not save the image. Check the server logs."));
        }
    }

    private boolean isValidImage(byte[] bytes, String contentType) {
        if (bytes.length < 12) {
            return false;
        }

        if ("image/jpeg".equals(contentType)) {
            return (bytes[0] & 0xFF) == 0xFF
                    && (bytes[1] & 0xFF) == 0xD8
                    && (bytes[2] & 0xFF) == 0xFF;
        }

        if ("image/png".equals(contentType)) {
            return (bytes[0] & 0xFF) == 0x89
                    && bytes[1] == 0x50
                    && bytes[2] == 0x4E
                    && bytes[3] == 0x47;
        }

        if ("image/webp".equals(contentType)) {
            return bytes[0] == 'R'
                    && bytes[1] == 'I'
                    && bytes[2] == 'F'
                    && bytes[3] == 'F'
                    && bytes[8] == 'W'
                    && bytes[9] == 'E'
                    && bytes[10] == 'B'
                    && bytes[11] == 'P';
        }

        return false;
    }

    @PostMapping
    public ResponseEntity<?> addProduct(
            @RequestBody Product product) {
        try {
            return ResponseEntity.ok(
                    productService.addProduct(product)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Integer id,
            @RequestBody Product product) {
        try {
            return ResponseEntity.ok(
                    productService.updateProduct(id, product)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Integer id) {
        try {
            productService.deleteProduct(id);

            return ResponseEntity.ok(
                    Map.of("message",
                            "Product deleted successfully")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
