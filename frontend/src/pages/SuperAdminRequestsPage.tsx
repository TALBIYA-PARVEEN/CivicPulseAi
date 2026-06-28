import { useEffect, useState } from 'react';
import { api } from '../api/client';
import type { AdminRequest } from '../types';
import { formatDate } from '../components/badges';
import { EmptyState, ErrorBanner, PageContainer, Spinner } from '../components/ui';

export default function SuperAdminRequestsPage() {
  const [requests, setRequests] = useState<AdminRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [assigning, setAssigning] = useState<number | null>(null);
  const [adminIdInput, setAdminIdInput] = useState<Record<number, string>>({});

  async function load() {
    try {
      setRequests(await api.getPendingRequests());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load requests');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function handleAssign(requestId: number) {
    const adminId = adminIdInput[requestId];
    if (!adminId) {
      setError('Enter an admin user ID first');
      return;
    }
    setAssigning(requestId);
    try {
      await api.assignAdmin(requestId, Number(adminId));
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Assignment failed');
    } finally {
      setAssigning(null);
    }
  }

  return (
    <PageContainer
      title="Admin Requests"
      subtitle="Pending requests for area administrators"
    >
      {error && <ErrorBanner message={error} />}
      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : requests.length === 0 ? (
        <EmptyState message="No pending admin requests." />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {requests.map((r) => (
            <div key={r.id} className="card p-5">
              <div className="mb-2 flex items-center justify-between">
                <h3 className="font-semibold text-slate-900">
                  {r.city} — {r.area}
                </h3>
                <span className="badge bg-amber-100 text-amber-800">
                  {r.status}
                </span>
              </div>
              <p className="text-sm text-slate-500">
                {r.issueCount} issue{r.issueCount !== 1 ? 's' : ''} pending
              </p>
              <p className="mt-1 text-xs text-slate-400">
                Created {formatDate(r.createdAt)}
              </p>
              {r.assignedAdmin && (
                <p className="mt-2 text-xs text-emerald-600">
                  Assigned to {r.assignedAdmin.email}
                </p>
              )}
              <div className="mt-4 flex gap-2">
                <input
                  type="number"
                  placeholder="Admin user ID"
                  value={adminIdInput[r.id] ?? ''}
                  onChange={(e) =>
                    setAdminIdInput((prev) => ({
                      ...prev,
                      [r.id]: e.target.value,
                    }))
                  }
                  className="input text-sm"
                />
                <button
                  onClick={() => handleAssign(r.id)}
                  disabled={assigning === r.id}
                  className="btn-primary text-xs"
                >
                  {assigning === r.id ? <Spinner /> : 'Assign'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </PageContainer>
  );
}
