package com.example.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    public FileUpload saveFile(FileUpload fileUpload) {
        return fileUploadRepository.save(fileUpload);
    }

    public FileUpload getFileById(Long id) {
        return fileUploadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

    public void deleteFile(Long id) {
        FileUpload fileUpload = getFileById(id);
        fileUploadRepository.delete(fileUpload);
    }

    public List<FileUpload> getAllFiles() {
        return fileUploadRepository.findAll();
    }

    public FileUpload updateFile(Long id, FileUpload updatedFileUpload) {
        FileUpload existingFileUpload = getFileById(id);
        existingFileUpload.setFileName(updatedFileUpload.getFileName());
        existingFileUpload.setFileType(updatedFileUpload.getFileType());
        existingFileUpload.setFilePath(updatedFileUpload.getFilePath());
        existingFileUpload.setFileSize(updatedFileUpload.getFileSize());
        existingFileUpload.setUploadedAt(updatedFileUpload.getUploadedAt());
        return fileUploadRepository.save(existingFileUpload);
    }
}
