import type {
  AdminRequest,
  CommentResponse,
  CreateCommentRequest,
  CreateIssueRequest,
  ImageUploadResponse,
  IssueResponse,
  LoginRequest,
  LoginResponse,
  MapIssueDTO,
  NotificationResponse,
  RegisterRequest,
  RegisterResponse,
  SuperAdminDashboardDTO,
  UpdateIssueStatusRequest,
  VerificationResponse,
} from '../types';

const BASE = '/api';

let authToken: string | null = localStorage.getItem('civicpulse_token');

export function setToken(token: string | null) {
  authToken = token;
  if (token) localStorage.setItem('civicpulse_token', token);
  else localStorage.removeItem('civicpulse_token');
}

export function getToken(): string | null {
  return authToken;
}

function headers(json = true): HeadersInit {
  const h: Record<string, string> = {};
  if (json) h['Content-Type'] = 'application/json';
  if (authToken) h['Authorization'] = `Bearer ${authToken}`;
  return h;
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    headers: { ...headers(), ...options.headers },
  });
  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const text = await res.text();
      if (text) message = text;
    } catch {
      // ignore
    }
    throw new Error(message);
  }
  if (res.status === 204) return undefined as T;
  const ct = res.headers.get('content-type') || '';
  if (ct.includes('application/json')) return res.json() as Promise<T>;
  return (await res.text()) as unknown as T;
}

export const api = {
  // auth
  register: (body: RegisterRequest) =>
    request<RegisterResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  login: (body: LoginRequest) =>
    request<LoginResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(body),
    }),

  // issues
  createIssue: (body: CreateIssueRequest) =>
    request<IssueResponse>('/issues', {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  getAllIssues: () => request<IssueResponse[]>('/issues'),
  getIssueById: (id: number) => request<IssueResponse>(`/issues/${id}`),
  uploadImage: (issueId: number, file: File) => {
    const form = new FormData();
    form.append('file', file);
    return request<ImageUploadResponse>(`/issues/${issueId}/images`, {
      method: 'POST',
      body: form,
      headers: headers(false),
    });
  },
  updateIssueStatus: (issueId: number, body: UpdateIssueStatusRequest) =>
    request<IssueResponse>(`/issues/${issueId}/status`, {
      method: 'PUT',
      body: JSON.stringify(body),
    }),
  verifyIssue: (issueId: number) =>
    request<VerificationResponse>(`/issues/${issueId}/verify`, {
      method: 'POST',
    }),
  addComment: (issueId: number, body: CreateCommentRequest) =>
    request<CommentResponse>(`/issues/${issueId}/comments`, {
      method: 'POST',
      body: JSON.stringify(body),
    }),
  getComments: (issueId: number) =>
    request<CommentResponse[]>(`/issues/${issueId}/comments`),

  // notifications
  getNotifications: () => request<NotificationResponse[]>('/notifications'),

  // super-admin
  getDashboardSummary: () =>
    request<SuperAdminDashboardDTO>('/super-admin/summary'),
  getMapIssues: () => request<MapIssueDTO[]>('/super-admin/map'),
  getHeatmap: () => request<Record<string, number>>('/super-admin/heatmap'),
  getPendingRequests: () => request<AdminRequest[]>('/super-admin/requests'),
  assignAdmin: (requestId: number, adminId: number) =>
    request<string>(`/super-admin/assign?requestId=${requestId}&adminId=${adminId}`, {
      method: 'POST',
    }),

  // gis
  getGisHeatmap: () => request<unknown>('/gis/heatmap'),
};
