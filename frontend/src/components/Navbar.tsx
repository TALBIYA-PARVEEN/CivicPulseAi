import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { Role } from '../types';

const navByRole: Record<Role, { to: string; label: string }[]> = {
  CITIZEN: [
    { to: '/issues', label: 'Issues' },
    { to: '/issues/new', label: 'Report' },
    { to: '/notifications', label: 'Notifications' },
  ],
  ADMIN: [
    { to: '/admin/issues', label: 'Assigned Issues' },
    { to: '/notifications', label: 'Notifications' },
  ],
  SUPER_ADMIN: [
    { to: '/super-admin', label: 'Dashboard' },
    { to: '/super-admin/map', label: 'Map' },
    { to: '/super-admin/requests', label: 'Requests' },
    { to: '/notifications', label: 'Notifications' },
  ],
};

export default function Navbar() {
  const { user, logout, setRole } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;
  const links = navByRole[user.role] ?? navByRole.CITIZEN;

  return (
    <header className="sticky top-0 z-40 border-b border-slate-200 bg-white/90 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6">
        <div className="flex items-center gap-8">
          <button
            onClick={() => navigate('/')}
            className="flex items-center gap-2 text-lg font-bold text-primary-700"
          >
            <span className="grid h-8 w-8 place-items-center rounded-lg bg-primary-600 text-white">
              C
            </span>
            CivicPulse
          </button>
          <nav className="hidden gap-1 sm:flex">
            {links.map((l) => (
              <NavLink
                key={l.to}
                to={l.to}
                className={({ isActive }) =>
                  `rounded-lg px-3 py-2 text-sm font-medium transition ${
                    isActive
                      ? 'bg-primary-50 text-primary-700'
                      : 'text-slate-600 hover:bg-slate-100'
                  }`
                }
              >
                {l.label}
              </NavLink>
            ))}
          </nav>
        </div>
        <div className="flex items-center gap-3">
          <select
            value={user.role}
            onChange={(e) => setRole(e.target.value as Role)}
            className="hidden rounded-lg border border-slate-300 bg-white px-2 py-1 text-xs text-slate-600 sm:block"
            title="Switch role (demo)"
          >
            <option value="CITIZEN">Citizen</option>
            <option value="ADMIN">Admin</option>
            <option value="SUPER_ADMIN">Super Admin</option>
          </select>
          <div className="hidden text-right sm:block">
            <div className="text-sm font-medium text-slate-800">
              {user.name ?? user.email}
            </div>
            <div className="text-xs text-slate-500">{user.role}</div>
          </div>
          <button onClick={logout} className="btn-secondary text-xs">
            Logout
          </button>
        </div>
      </div>
      <nav className="flex gap-1 overflow-x-auto px-4 pb-2 sm:hidden">
        {links.map((l) => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) =>
              `whitespace-nowrap rounded-lg px-3 py-1.5 text-sm font-medium transition ${
                isActive
                  ? 'bg-primary-50 text-primary-700'
                  : 'text-slate-600 hover:bg-slate-100'
              }`
            }
          >
            {l.label}
          </NavLink>
        ))}
      </nav>
    </header>
  );
}
