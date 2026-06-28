export type Role = 'CITIZEN' | 'ADMIN' | 'SUPER_ADMIN';

export type IssueCategory =
  | 'ROAD'
  | 'WATER'
  | 'ELECTRICITY'
  | 'GARBAGE'
  | 'SEWAGE'
  | 'OTHER';

export type IssueStatus = 'REPORTED' | 'UNDER_REVIEW' | 'IN_PROGRESS' | 'RESOLVED';

export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export type RequestStatus = 'PENDING' | 'ASSIGNED';

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  id: number;
  name: string;
  email: string;
  role: Role;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
}

export interface CreateIssueRequest {
  title: string;
  description: string;
  latitude?: number | null;
  longitude?: number | null;
  category: IssueCategory;
  city: string;
  area: string;
}

export interface IssueResponse {
  id: number;
  title: string;
  description: string;
  latitude: number | null;
  longitude: number | null;
  category: IssueCategory;
  status: IssueStatus;
  createdAt: string;
  reportedBy: string;
  imageUrls: string[];
  aiSeverity: string | null;
  aiDepartment: string | null;
  isDuplicate: boolean | null;
  masterIssueId: number | null;
  verificationCount: number;
}

export interface UpdateIssueStatusRequest {
  status: IssueStatus;
}

export interface ImageUploadResponse {
  imageId: number;
  imageUrl: string;
}

export interface VerificationResponse {
  issueId: number;
  verificationCount: number;
}

export interface CreateCommentRequest {
  commentText: string;
}

export interface CommentResponse {
  id: number;
  commentText: string;
  userEmail: string;
  createdAt: string;
}

export interface NotificationResponse {
  id: number;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export interface MapIssueDTO {
  id: number;
  category: string;
  status: string;
  latitude: number | null;
  longitude: number | null;
  aiSeverity: string | null;
}

export interface SuperAdminDashboardDTO {
  totalIssues: number;
  resolvedIssues: number;
  pendingIssues: number;
  overdueIssues: number;
}

export interface AdminRequest {
  id: number;
  city: string;
  area: string;
  issueCount: number;
  status: RequestStatus;
  createdAt: string;
  updatedAt: string | null;
  assignedAdmin: { id: number; name: string; email: string } | null;
}

export interface AuthUser {
  email: string;
  role: Role;
  name?: string;
}
