import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';

export default function HomePage() {
  const { user } = useAuth();
  if (!user) {
    return (
      <div className="grid min-h-screen place-items-center bg-gradient-to-br from-primary-50 via-white to-accent-50 px-4">
        <div className="max-w-lg text-center">
          <div className="mx-auto mb-4 grid h-16 w-16 place-items-center rounded-2xl bg-primary-600 text-2xl font-bold text-white">
            C
          </div>
          <h1 className="text-3xl font-bold text-slate-900">CivicPulse</h1>
          <p className="mt-3 text-slate-600">
            AI-powered civic issue management. Report problems, track progress,
            and keep your community thriving.
          </p>
          <div className="mt-6 flex justify-center gap-3">
            <Link to="/login" className="btn-primary">
              Sign in
            </Link>
            <Link to="/register" className="btn-secondary">
              Create account
            </Link>
          </div>
        </div>
      </div>
    );
  }
  return null;
}
