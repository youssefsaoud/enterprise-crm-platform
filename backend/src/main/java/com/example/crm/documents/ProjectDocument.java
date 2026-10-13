package com.example.crm.documents;

import com.example.crm.common.model.AuditSupport;
import com.example.crm.projects.Project;
import com.example.crm.quotations.Quotation;
import com.example.crm.users.User;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_documents")
public class ProjectDocument extends AuditSupport {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "project_id", nullable = false) private Project project;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "quotation_id") private Quotation quotation;
    @Column(nullable = false, length = 255) private String originalFilename;
    @Column(nullable = false, length = 120, unique = true) private String storageKey;
    @Column(nullable = false, length = 120) private String contentType;
    @Column(nullable = false) private long fileSize;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "uploaded_by", nullable = false) private User uploadedBy;

    protected ProjectDocument() {}
    public ProjectDocument(Project project, Quotation quotation, String originalFilename, String storageKey, String contentType, long fileSize, User uploadedBy) {
        this.project = project; this.quotation = quotation; this.originalFilename = originalFilename; this.storageKey = storageKey; this.contentType = contentType; this.fileSize = fileSize; this.uploadedBy = uploadedBy;
    }
    @Override protected void assignIdIfNecessary() { if (id == null) id = newId(); }
    public UUID getId() { return id; }
    public Project getProject() { return project; }
    public Quotation getQuotation() { return quotation; }
    public String getOriginalFilename() { return originalFilename; }
    public String getStorageKey() { return storageKey; }
    public String getContentType() { return contentType; }
    public long getFileSize() { return fileSize; }
    public User getUploadedBy() { return uploadedBy; }
}
