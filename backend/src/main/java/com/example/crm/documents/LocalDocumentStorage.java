package com.example.crm.documents;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalDocumentStorage implements DocumentStorage {
    private final Path root;

    public LocalDocumentStorage(@Value("${crm.documents.storage-path:${user.home}/.crm/uploads}") String storagePath) {
        this.root = Path.of(storagePath).toAbsolutePath().normalize();
    }

    @Override
    public String store(InputStream content, String extension) throws IOException {
        Files.createDirectories(root);
        String storageKey = UUID.randomUUID() + extension;
        Files.copy(content, root.resolve(storageKey), StandardCopyOption.CREATE_NEW);
        return storageKey;
    }

    @Override
    public Path resolve(String storageKey) {
        Path resolved = root.resolve(storageKey).normalize();
        if (!resolved.getParent().equals(root) || resolved.getFileName().toString().contains("..")) throw new IllegalArgumentException("Invalid document key");
        return resolved;
    }
}
