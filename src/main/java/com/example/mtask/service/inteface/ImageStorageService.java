package com.example.mtask.service.inteface;

import com.example.mtask.enums.LogoType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for image storage services.
 * <p>
 * This interface defines the contract for managing image files, including upload, deletion, and retrieval.
 * Implementations of this interface can use various storage solutions (e.g., cloud storage, local file systems).
 * </p>
 */
public interface ImageStorageService {

    /**
     * Uploads an image file to the storage.
     *
     * @param file     the image file to upload
     * @param logoType the type of the logo, which determines the storage bucket or folder
     * @return the path or URL of the uploaded image
     */
    String uploadImage(MultipartFile file, LogoType logoType);

    /**
     * Deletes an image file from the storage.
     *
     * @param fileName the name or path of the file to delete
     * @throws RuntimeException if an error occurs during the deletion process
     */
    void deleteImage(String fileName);

    /**
     * Downloads an image file from the storage.
     *
     * @param imagePath the path or name of the file to download
     * @return the file's content as a byte array
     * @throws RuntimeException if an error occurs during the download process
     */
    byte[] downloadImage(String imagePath);
}
