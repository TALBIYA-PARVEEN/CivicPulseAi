import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { SuperAdminDashboardDTO } from '../types';
import { ErrorBanner, PageContainer, Spinner } from '../components/ui';

function StatCard({
  label,
  value,
  accent,
  icon,
}: {
  label: string;
  value: number;
  accent: string;
  icon: string;
}) {
  return (
    <div className="card p-5">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-slate-500">{label}</p>
          <p className="mt-1 text-3xl font-bold text-slate-900">{value}</p>
        </div>
        <div
          className={`grid h-12 w-12 place-items-center rounded-xl text-xl ${accent}`}
        >
          {icon}
        </div>
      </div>
    </div>
  );
}

export default function SuperAdminDashboardPage() {
  const [summary, setSummary] = useState<SuperAdminDashboardDTO | null>(null);
  const [heatmap, setHeatmap] = useState<Record<string, number>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([api.getDashboardSummary(), api.getHeatmap()])
      .then(([s, h]) => {
        setSummary(s);
        setHeatmap(h);
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const heatmapEntries = Object.entries(heatmap).sort((a, b) => b[1] - a[1]);
  const maxCount = heatmapEntries[0]?.[1] ?? 1;

  return (
    <PageContainer
      title="Super Admin Dashboard"
      subtitle="Overview of all civic issues across the system"
      actions={
        <div className="flex gap-2">
          <Link to="/super-admin/map" className="btn-secondary">
            View Map
          </Link>
          <Link to="/super-admin/requests" className="btn-secondary">
            Admin Requests
          </Link>
        </div>
      }
    >
      {error && <ErrorBanner message={error} />}
      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <StatCard
              label="Total Issues"
              value={summary?.totalIssues ?? 0}
              accent="bg-primary-100 text-primary-700"
              icon="📋"
            />
            <StatCard
              label="Resolved"
              value={summary?.resolvedIssues ?? 0}
              accent="bg-emerald-100 text-emerald-700"
              icon="✓"
            />
            <StatCard
              label="Pending"
              value={summary?.pendingIssues ?? 0}
              accent="bg-amber-100 text-amber-700"
              icon="⏳"
            />
            <StatCard
              label="Overdue"
              value={summary?.overdueIssues ?? 0}
              accent="bg-red-100 text-red-700"
              icon="⚠"
            />
          </div>

          <div className="mt-8 grid gap-6 lg:grid-cols-2">
            <div className="card p-5">
              <h2 className="mb-4 text-lg font-semibold text-slate-800">
                Area Heatmap
              </h2>
              {heatmapEntries.length === 0 ? (
                <p className="text-sm text-slate-400">No data available.</p>
              ) : (
                <div className="space-y-2">
                  {heatmapEntries.slice(0, 10).map(([area, count]) => (
                    <div key={area}>
                      <div className="mb-1 flex justify-between text-sm">
                        <span className="text-slate-600">{area}</span>
                        <span className="font-medium text-slate-800">
                          {count}
                        </span>
                      </div>
                      <div className="h-2 overflow-hidden rounded-full bg-slate-100">
                        <div
                          className="h-full rounded-full bg-gradient-to-r from-primary-400 to-primary-600 transition-all"
                          style={{ width: `${(count / maxCount) * 100}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="card p-5">
              <h2 className="mb-4 text-lg font-semibold text-slate-800">
                Resolution Rate
              </h2>
              {summary && summary.totalIssues > 0 ? (
                <div>
                  <div className="mb-2 flex items-end justify-between">
                    <span className="text-3xl font-bold text-slate-900">
                      {Math.round(
                        (summary.resolvedIssues / summary.totalIssues) * 100,
                      )}
                      %
                    </span>
                    <span className="text-sm text-slate-500">
                      {summary.resolvedIssues} of {summary.totalIssues}
                    </span>
                  </div>
                  <div className="h-3 overflow-hidden rounded-full bg-slate-100">
                    <div
                      className="h-full rounded-full bg-gradient-to-r from-emerald-400 to-emerald-600 transition-all"
                      style={{
                        width: `${(summary.resolvedIssues / summary.totalIssues) * 100}%`,
                      }}
                    />
                  </div>
                </div>
              ) : (
                <p className="text-sm text-slate-400">No issues to measure.</p>
              )}
            </div>
          </div>
        </>
      )}
    </PageContainer>
  );
}
