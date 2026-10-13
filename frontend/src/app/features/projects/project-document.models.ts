export interface ProjectDocument {
  id: string;
  projectId: string;
  quotationId?: string | null;
  originalFilename: string;
  contentType: string;
  fileSize: number;
  uploadedById: string;
  uploadedByName: string;
  uploadedAt: string;
}
