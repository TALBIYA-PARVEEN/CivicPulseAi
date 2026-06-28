import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ErrorBanner, Spinner } from '../components/ui';

export default function LoginPage() {
  const { login, loading } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    try {
      await login(email, password);
      navigate('/');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Login failed');
    }
  }

  return (
    <div className="grid min-h-screen place-items-center bg-gradient-to-br from-primary-50 via-white to-accent-50 px-4">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <div className="mx-auto mb-3 grid h-12 w-12 place-items-center rounded-xl bg-primary-600 text-xl font-bold text-white">
            C
          </div>
          <h1 className="text-2xl font-bold text-slate-900">CivicPulse</h1>
          <p className="mt-1 text-sm text-slate-500">
            Sign in to your civic issue account
          </p>
        </div>
        <form onSubmit={handleSubmit} className="card space-y-4 p-6">
          {error && <ErrorBanner message={error} />}
          <div>
            <label className="label">Email</label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input"
              placeholder="you@example.com"
            />
          </div>
          <div>
            <label className="label">Password</label>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input"
              placeholder="••••••••"
            />
          </div>
          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? <Spinner /> : 'Sign in'}
          </button>
          <p className="text-center text-sm text-slate-500">
            No account?{' '}
            <button
              type="button"
              onClick={() => navigate('/register')}
              className="font-medium text-primary-600 hover:text-primary-700"
            >
              Register
            </button>
          </p>
        </form>
      </div>
    </div>
  );
}
