import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import RequireAuth from './components/RequireAuth';
import RoleGuard from './components/RoleGuard';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import IssuesListPage from './pages/IssuesListPage';
import CreateIssuePage from './pages/CreateIssuePage';
import IssueDetailPage from './pages/IssueDetailPage';
import AdminIssuesPage from './pages/AdminIssuesPage';
import NotificationsPage from './pages/NotificationsPage';
import SuperAdminDashboardPage from './pages/SuperAdminDashboardPage';
import SuperAdminMapPage from './pages/SuperAdminMapPage';
import SuperAdminRequestsPage from './pages/SuperAdminRequestsPage';

function RoleRedirect() {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  switch (user.role) {
    case 'SUPER_ADMIN':
      return <Navigate to="/super-admin" replace />;
    case 'ADMIN':
      return <Navigate to="/admin/issues" replace />;
    default:
      return <Navigate to="/issues" replace />;
  }
}

function AppShell() {
  const { user } = useAuth();
  return (
    <div className="flex min-h-screen flex-col">
      {user && <Navbar />}
      <main className="flex-1">
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/" element={<RoleRedirect />} />

          <Route
            path="/issues"
            element={
              <RequireAuth>
                <IssuesListPage />
              </RequireAuth>
            }
          />
          <Route
            path="/issues/new"
            element={
              <RequireAuth>
                <CreateIssuePage />
              </RequireAuth>
            }
          />
          <Route
            path="/issues/:id"
            element={
              <RequireAuth>
                <IssueDetailPage />
              </RequireAuth>
            }
          />
          <Route
            path="/notifications"
            element={
              <RequireAuth>
                <NotificationsPage />
              </RequireAuth>
            }
          />

          <Route
            path="/admin/issues"
            element={
              <RequireAuth>
                <RoleGuard roles={['ADMIN', 'SUPER_ADMIN']}>
                  <AdminIssuesPage />
                </RoleGuard>
              </RequireAuth>
            }
          />

          <Route
            path="/super-admin"
            element={
              <RequireAuth>
                <RoleGuard roles={['SUPER_ADMIN']}>
                  <SuperAdminDashboardPage />
                </RoleGuard>
              </RequireAuth>
            }
          />
          <Route
            path="/super-admin/map"
            element={
              <RequireAuth>
                <RoleGuard roles={['SUPER_ADMIN']}>
                  <SuperAdminMapPage />
                </RoleGuard>
              </RequireAuth>
            }
          />
          <Route
            path="/super-admin/requests"
            element={
              <RequireAuth>
                <RoleGuard roles={['SUPER_ADMIN']}>
                  <SuperAdminRequestsPage />
                </RoleGuard>
              </RequireAuth>
            }
          />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppShell />
      </BrowserRouter>
    </AuthProvider>
  );
}
