import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import type { AuthUser, Role } from '../types';
import { api, setToken } from '../api/client';

interface AuthContextValue {
  user: AuthUser | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  setRole: (role: Role) => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

const STORAGE_USER = 'civicpulse_user';

function decodeJwtEmail(token: string): string | null {
  try {
    const payload = token.split('.')[1];
    const json = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return json.sub ?? null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(() => {
    const raw = localStorage.getItem(STORAGE_USER);
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('civicpulse_token');
    if (token && !user) {
      const email = decodeJwtEmail(token);
      if (email) {
        const fallback: AuthUser = { email, role: 'CITIZEN' };
        setUser(fallback);
        localStorage.setItem(STORAGE_USER, JSON.stringify(fallback));
      }
    }
  }, [user]);

  const login = useCallback(async (email: string, password: string) => {
    setLoading(true);
    try {
      const { token } = await api.login({ email, password });
      setToken(token);
      const decodedEmail = decodeJwtEmail(token) ?? email;
      const u: AuthUser = { email: decodedEmail, role: 'CITIZEN' };
      setUser(u);
      localStorage.setItem(STORAGE_USER, JSON.stringify(u));
    } finally {
      setLoading(false);
    }
  }, []);

  const register = useCallback(
    async (name: string, email: string, password: string) => {
      setLoading(true);
      try {
        const res = await api.register({ name, email, password });
        const { token } = await api.login({ email, password });
        setToken(token);
        const u: AuthUser = { email: res.email, name: res.name, role: res.role };
        setUser(u);
        localStorage.setItem(STORAGE_USER, JSON.stringify(u));
      } finally {
        setLoading(false);
      }
    },
    [],
  );

  const logout = useCallback(() => {
    setToken(null);
    localStorage.removeItem(STORAGE_USER);
    setUser(null);
  }, []);

  const setRole = useCallback((role: Role) => {
    setUser((prev) => {
      if (!prev) return prev;
      const next = { ...prev, role };
      localStorage.setItem(STORAGE_USER, JSON.stringify(next));
      return next;
    });
  }, []);

  const value = useMemo(
    () => ({ user, loading, login, register, logout, setRole }),
    [user, loading, login, register, logout, setRole],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
