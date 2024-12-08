package com.example.mtask.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LogoType {
    //TODO one bucket but different folders?
    PRODUCT("product-logos"),
    CATEGORY("category-logos");

    private final String bucketName;

    LogoType(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public static List<String> getAllBucketNames() {
        return Arrays.stream(LogoType.values())
                .map(LogoType::getBucketName)
                .collect(Collectors.toList());
    }
}