package com.example.mtask.dto.category;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class CategoryRcvDto {

    @NotNull
    private String name;

    private MultipartFile file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
