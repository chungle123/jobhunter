package vn.hoidanit.jobhunter.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.hoidanit.jobhunter.domain.dto.ResFileUploadDTO;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.service.errors.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${hoidanit.upload-file.base-path}")
    private String basePath;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<ResFileUploadDTO> upload(@RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        // validate
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty!");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new StorageException("File không đúng định dạng!");
        }
        // create folder
        this.fileService.createUploadFolder(basePath + folder);

        // save file
        String uploaded = this.fileService.store(file, folder);
        ResFileUploadDTO res = new ResFileUploadDTO(uploaded, Instant.now());
        return ResponseEntity.ok().body(res);
    }
}
