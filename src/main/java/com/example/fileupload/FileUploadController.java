package com.example.fileupload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    private final String uploadDir = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<FileUpload> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Ensure the upload directory exists
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }

            // Save the file to the server
            String fileName = file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            Path path = Paths.get(filePath);
            Files.write(path, file.getBytes());

            // Create FileUpload entity and save metadata
            FileUpload fileUpload = new FileUpload();
            fileUpload.setFileName(fileName);
            fileUpload.setFileType(file.getContentType());
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFilePath(filePath);
            fileUpload.setUploadedAt(LocalDateTime.now());

            FileUpload savedFile = fileUploadService.saveFile(fileUpload);
            return ResponseEntity.ok(savedFile);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            // Fetch the file metadata from the database
            FileUpload fileUpload = fileUploadService.getFileById(id);
            Path path = Paths.get(fileUpload.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(fileUpload.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileUpload.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        try {
            fileUploadService.deleteFile(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/update/{id}")
public ResponseEntity<FileUpload> updateFile(@PathVariable Long id, @RequestBody FileUpload updatedFileUpload) {
    try {
        FileUpload updatedFile = fileUploadService.updateFile(id, updatedFileUpload);
        return ResponseEntity.ok(updatedFile);
    } catch (Exception e) {
        return ResponseEntity.status(500).build();
    }
}
}
