import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { IssueResponse, IssueStatus } from '../types';
import {
  CATEGORY_COLORS,
  CATEGORY_LABELS,
  STATUS_COLORS,
  STATUS_LABELS,
  formatDate,
} from '../components/badges';
import { EmptyState, ErrorBanner, PageContainer, Spinner } from '../components/ui';

const STATUSES: IssueStatus[] = [
  'REPORTED',
  'UNDER_REVIEW',
  'IN_PROGRESS',
  'RESOLVED',
];

export default function AdminIssuesPage() {
  const [issues, setIssues] = useState<IssueResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updating, setUpdating] = useState<number | null>(null);

  async function load() {
    try {
      setIssues(await api.getAllIssues());
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load issues');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function handleStatus(id: number, status: IssueStatus) {
    setUpdating(id);
    try {
      const updated = await api.updateIssueStatus(id, { status });
      setIssues((prev) => prev.map((i) => (i.id === id ? updated : i)));
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Update failed');
    } finally {
      setUpdating(null);
    }
  }

  return (
    <PageContainer
      title="Assigned Issues"
      subtitle="Review and update the status of issues in your area"
    >
      {error && <ErrorBanner message={error} />}
      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : issues.length === 0 ? (
        <EmptyState message="No issues assigned to you yet." />
      ) : (
        <div className="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-xs uppercase text-slate-500">
              <tr>
                <th className="px-4 py-3">Issue</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Status</th>
                <th className="px-4 py-3">Created</th>
                <th className="px-4 py-3">Update Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {issues.map((i) => (
                <tr key={i.id} className="hover:bg-slate-50">
                  <td className="px-4 py-3">
                    <Link
                      to={`/issues/${i.id}`}
                      className="font-medium text-primary-700 hover:underline"
                    >
                      {i.title}
                    </Link>
                    <p className="line-clamp-1 text-xs text-slate-400">
                      {i.description}
                    </p>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`badge ${CATEGORY_COLORS[i.category]}`}>
                      {CATEGORY_LABELS[i.category]}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`badge ${STATUS_COLORS[i.status]}`}>
                      {STATUS_LABELS[i.status]}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-slate-500">
                    {formatDate(i.createdAt)}
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex flex-wrap gap-1">
                      {STATUSES.map((s) => (
                        <button
                          key={s}
                          onClick={() => handleStatus(i.id, s)}
                          disabled={updating === i.id || i.status === s}
                          className={`rounded px-2 py-1 text-xs font-medium transition ${
                            i.status === s
                              ? 'bg-primary-600 text-white'
                              : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                          }`}
                        >
                          {STATUS_LABELS[s]}
                        </button>
                      ))}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </PageContainer>
  );
}
