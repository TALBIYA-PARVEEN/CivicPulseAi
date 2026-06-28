import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { api } from '../api/client';
import type { IssueCategory, IssueResponse, IssueStatus } from '../types';
import {
  CATEGORY_COLORS,
  CATEGORY_LABELS,
  STATUS_COLORS,
  STATUS_LABELS,
  formatDate,
} from '../components/badges';
import { EmptyState, ErrorBanner, PageContainer, Spinner } from '../components/ui';

const CATEGORIES: (IssueCategory | 'ALL')[] = [
  'ALL',
  'ROAD',
  'WATER',
  'ELECTRICITY',
  'GARBAGE',
  'SEWAGE',
  'OTHER',
];

const STATUSES: (IssueStatus | 'ALL')[] = [
  'ALL',
  'REPORTED',
  'UNDER_REVIEW',
  'IN_PROGRESS',
  'RESOLVED',
];

export default function IssuesListPage() {
  const [issues, setIssues] = useState<IssueResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [category, setCategory] = useState<IssueCategory | 'ALL'>('ALL');
  const [status, setStatus] = useState<IssueStatus | 'ALL'>('ALL');
  const [search, setSearch] = useState('');

  useEffect(() => {
    api
      .getAllIssues()
      .then(setIssues)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const filtered = issues.filter((i) => {
    if (category !== 'ALL' && i.category !== category) return false;
    if (status !== 'ALL' && i.status !== status) return false;
    if (search) {
      const q = search.toLowerCase();
      if (
        !i.title.toLowerCase().includes(q) &&
        !i.description.toLowerCase().includes(q)
      )
        return false;
    }
    return true;
  });

  return (
    <PageContainer
      title="Civic Issues"
      subtitle="Browse and track reported issues in your area"
      actions={
        <Link to="/issues/new" className="btn-primary">
          + Report Issue
        </Link>
      }
    >
      {error && <ErrorBanner message={error} />}

      <div className="mb-4 flex flex-wrap gap-3">
        <input
          placeholder="Search title or description..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="input max-w-xs"
        />
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value as IssueCategory | 'ALL')}
          className="input max-w-[180px]"
        >
          {CATEGORIES.map((c) => (
            <option key={c} value={c}>
              {c === 'ALL' ? 'All categories' : CATEGORY_LABELS[c]}
            </option>
          ))}
        </select>
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value as IssueStatus | 'ALL')}
          className="input max-w-[180px]"
        >
          {STATUSES.map((s) => (
            <option key={s} value={s}>
              {s === 'ALL' ? 'All statuses' : STATUS_LABELS[s]}
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : filtered.length === 0 ? (
        <EmptyState message="No issues match your filters." />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((issue) => (
            <Link
              key={issue.id}
              to={`/issues/${issue.id}`}
              className="card group block p-4 transition hover:shadow-md hover:ring-1 hover:ring-primary-200"
            >
              <div className="mb-2 flex items-center justify-between gap-2">
                <span
                  className={`badge ${CATEGORY_COLORS[issue.category]}`}
                >
                  {CATEGORY_LABELS[issue.category]}
                </span>
                <span className={`badge ${STATUS_COLORS[issue.status]}`}>
                  {STATUS_LABELS[issue.status]}
                </span>
              </div>
              <h3 className="mb-1 line-clamp-1 font-semibold text-slate-900 group-hover:text-primary-700">
                {issue.title}
              </h3>
              <p className="mb-3 line-clamp-2 text-sm text-slate-500">
                {issue.description}
              </p>
              <div className="flex items-center justify-between text-xs text-slate-400">
                <span>{formatDate(issue.createdAt)}</span>
                <span className="flex items-center gap-2">
                  {issue.aiSeverity && (
                    <span className="font-medium text-slate-600">
                      {issue.aiSeverity}
                    </span>
                  )}
                  <span>✓ {issue.verificationCount}</span>
                </span>
              </div>
            </Link>
          ))}
        </div>
      )}
    </PageContainer>
  );
}
