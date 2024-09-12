package com.example.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private UserRepository userRepository;

    public FileUpload saveFile(Long userId, FileUpload fileUpload) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        fileUpload.setUser(user);
        return fileUploadRepository.save(fileUpload);
    }

    public FileUpload getFileById(Long id, Long userId) {
        FileUpload fileUpload = fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
        if (!fileUpload.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to file");
        }
        return fileUpload;
    }

    public void deleteFile(Long id, Long userId) {
        FileUpload fileUpload = getFileById(id, userId);
        fileUploadRepository.delete(fileUpload);
    }

    public List<FileUpload> getAllFilesByUser(Long userId) {
        return fileUploadRepository.findByUserId(userId);
    }

    public FileUpload updateFile(Long id, FileUpload updatedFileUpload, Long userId) {
        FileUpload existingFileUpload = getFileById(id, userId);
        existingFileUpload.setFileName(updatedFileUpload.getFileName());
        existingFileUpload.setFileType(updatedFileUpload.getFileType());
        existingFileUpload.setFilePath(updatedFileUpload.getFilePath());
        existingFileUpload.setFileSize(updatedFileUpload.getFileSize());
        existingFileUpload.setUploadedAt(updatedFileUpload.getUploadedAt());
        return fileUploadRepository.save(existingFileUpload);
    }
}
