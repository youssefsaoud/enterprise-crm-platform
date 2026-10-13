package com.example.crm.documents;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface DocumentStorage {
    String store(InputStream content, String extension) throws IOException;
    Path resolve(String storageKey);
}
