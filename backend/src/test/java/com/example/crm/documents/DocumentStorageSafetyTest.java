package com.example.crm.documents;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.crm.common.web.ConflictException;
import com.example.crm.projects.*;
import com.example.crm.quotations.QuotationRepository;
import com.example.crm.security.CrmUserPrincipal;
import com.example.crm.users.*;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class DocumentStorageSafetyTest {
    @TempDir Path root;

    @Test void storageRoundTripsBytesAndRejectsTraversalKeys() throws Exception {
        var storage = new LocalDocumentStorage(root.toString());
        byte[] bytes = "Synthetic scope".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String key = storage.store(new ByteArrayInputStream(bytes), ".txt");
        assertThat(Files.readAllBytes(storage.resolve(key))).isEqualTo(bytes);
        assertThat(storage.resolve(key).getParent()).isEqualTo(root);
        assertThatThrownBy(() -> storage.resolve("../escape.txt")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> storage.resolve(root.getParent().resolve("escape.txt").toString())).isInstanceOf(IllegalArgumentException.class);
    }

    @Test void invalidTypeEmptyAndOversizedFilesNeverReachStorage() {
        var projects = mock(ProjectRepository.class); var storage = mock(DocumentStorage.class);
        var principal = mock(CrmUserPrincipal.class); when(principal.role()).thenReturn(Role.ADMIN);
        UUID id = UUID.randomUUID(); when(projects.findById(id)).thenReturn(Optional.of(new Project("Test", "TEST", null)));
        var service = new ProjectDocumentService(mock(ProjectDocumentRepository.class), projects, mock(QuotationRepository.class), mock(UserRepository.class), storage);
        var empty = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        var unsupported = new MockMultipartFile("file", "test.exe", "application/octet-stream", new byte[]{1});
        var large = mock(MultipartFile.class); when(large.getSize()).thenReturn(10L * 1024 * 1024 + 1);
        for (MultipartFile file : new MultipartFile[]{empty, unsupported, large}) {
            assertThatThrownBy(() -> service.upload(id, null, file, principal)).isInstanceOf(ConflictException.class);
        }
        verifyNoInteractions(storage);
    }
}
