import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import type {
  CommentResponse,
  IssueResponse,
  IssueStatus,
} from '../types';
import {
  CATEGORY_COLORS,
  CATEGORY_LABELS,
  STATUS_COLORS,
  STATUS_LABELS,
  formatDate,
} from '../components/badges';
import { ErrorBanner, Spinner } from '../components/ui';

const STATUSES: IssueStatus[] = [
  'REPORTED',
  'UNDER_REVIEW',
  'IN_PROGRESS',
  'RESOLVED',
];

export default function IssueDetailPage() {
  const { id } = useParams<{ id: string }>();
  const issueId = Number(id);
  const [issue, setIssue] = useState<IssueResponse | null>(null);
  const [comments, setComments] = useState<CommentResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [commentText, setCommentText] = useState('');
  const [posting, setPosting] = useState(false);
  const [verifying, setVerifying] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [statusUpdating, setStatusUpdating] = useState(false);

  async function load() {
    try {
      const [i, c] = await Promise.all([
        api.getIssueById(issueId),
        api.getComments(issueId),
      ]);
      setIssue(i);
      setComments(c);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Failed to load issue');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [issueId]);

  async function handleVerify() {
    setVerifying(true);
    try {
      await api.verifyIssue(issueId);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Verification failed');
    } finally {
      setVerifying(false);
    }
  }

  async function handleComment(e: React.FormEvent) {
    e.preventDefault();
    if (!commentText.trim()) return;
    setPosting(true);
    try {
      await api.addComment(issueId, { commentText });
      setCommentText('');
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Comment failed');
    } finally {
      setPosting(false);
    }
  }

  async function handleUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setUploading(true);
    try {
      await api.uploadImage(issueId, file);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Upload failed');
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  }

  async function handleStatusChange(newStatus: IssueStatus) {
    setStatusUpdating(true);
    try {
      const updated = await api.updateIssueStatus(issueId, { status: newStatus });
      setIssue(updated);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Status update failed');
    } finally {
      setStatusUpdating(false);
    }
  }

  if (loading) {
    return (
      <div className="grid place-items-center py-20">
        <Spinner className="h-8 w-8" />
      </div>
    );
  }

  if (!issue) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-10">
        <ErrorBanner message={error || 'Issue not found'} />
        <Link to="/issues" className="btn-secondary mt-4">
          Back to issues
        </Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-4xl px-4 py-6 sm:px-6">
      {error && <ErrorBanner message={error} />}
      <Link
        to="/issues"
        className="mb-4 inline-block text-sm text-primary-600 hover:text-primary-700"
      >
        ← Back to issues
      </Link>

      <div className="card p-6">
        <div className="mb-3 flex flex-wrap items-center gap-2">
          <span className={`badge ${CATEGORY_COLORS[issue.category]}`}>
            {CATEGORY_LABELS[issue.category]}
          </span>
          <span className={`badge ${STATUS_COLORS[issue.status]}`}>
            {STATUS_LABELS[issue.status]}
          </span>
          {issue.aiSeverity && (
            <span className="badge bg-orange-100 text-orange-800">
              {issue.aiSeverity}
            </span>
          )}
          {issue.isDuplicate && (
            <span className="badge bg-amber-100 text-amber-800">Duplicate</span>
          )}
        </div>
        <h1 className="text-2xl font-bold text-slate-900">{issue.title}</h1>
        <p className="mt-3 whitespace-pre-wrap text-slate-600">
          {issue.description}
        </p>

        <div className="mt-5 grid gap-4 text-sm sm:grid-cols-2">
          <div>
            <span className="text-slate-400">Reported by</span>
            <p className="font-medium text-slate-700">{issue.reportedBy}</p>
          </div>
          <div>
            <span className="text-slate-400">Created</span>
            <p className="font-medium text-slate-700">
              {formatDate(issue.createdAt)}
            </p>
          </div>
          {issue.aiDepartment && (
            <div>
              <span className="text-slate-400">AI Department</span>
              <p className="font-medium text-slate-700">{issue.aiDepartment}</p>
            </div>
          )}
          <div>
            <span className="text-slate-400">Verifications</span>
            <p className="font-medium text-slate-700">
              {issue.verificationCount}
            </p>
          </div>
        </div>

        <div className="mt-6 flex flex-wrap gap-3">
          <button
            onClick={handleVerify}
            disabled={verifying}
            className="btn-secondary"
          >
            {verifying ? <Spinner /> : `Verify (✓ ${issue.verificationCount})`}
          </button>
          <label className="btn-secondary cursor-pointer">
            {uploading ? <Spinner /> : 'Upload Image'}
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={handleUpload}
            />
          </label>
        </div>
      </div>

      {issue.imageUrls.length > 0 && (
        <div className="mt-6">
          <h2 className="mb-3 text-lg font-semibold text-slate-800">Images</h2>
          <div className="grid gap-3 sm:grid-cols-3">
            {issue.imageUrls.map((url, idx) => (
              <a
                key={idx}
                href={url}
                target="_blank"
                rel="noreferrer"
                className="card overflow-hidden"
              >
                <img
                  src={url}
                  alt={`Issue ${issue.id} image ${idx + 1}`}
                  className="h-40 w-full object-cover"
                />
              </a>
            ))}
          </div>
        </div>
      )}

      <div className="mt-6 card p-6">
        <h2 className="mb-3 text-lg font-semibold text-slate-800">
          Update Status
        </h2>
        <div className="flex flex-wrap gap-2">
          {STATUSES.map((s) => (
            <button
              key={s}
              onClick={() => handleStatusChange(s)}
              disabled={statusUpdating || issue.status === s}
              className={`btn ${
                issue.status === s
                  ? 'bg-primary-600 text-white'
                  : 'btn-secondary'
              }`}
            >
              {STATUS_LABELS[s]}
            </button>
          ))}
        </div>
      </div>

      <div className="mt-6 card p-6">
        <h2 className="mb-4 text-lg font-semibold text-slate-800">
          Comments ({comments.length})
        </h2>
        <form onSubmit={handleComment} className="mb-4 flex gap-2">
          <input
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
            placeholder="Add a comment..."
            className="input"
          />
          <button type="submit" disabled={posting} className="btn-primary">
            {posting ? <Spinner /> : 'Post'}
          </button>
        </form>
        <div className="space-y-3">
          {comments.length === 0 ? (
            <p className="text-sm text-slate-400">No comments yet.</p>
          ) : (
            comments.map((c) => (
              <div
                key={c.id}
                className="rounded-lg border border-slate-100 bg-slate-50 p-3"
              >
                <div className="mb-1 flex items-center justify-between text-xs text-slate-500">
                  <span className="font-medium text-slate-700">
                    {c.userEmail}
                  </span>
                  <span>{formatDate(c.createdAt)}</span>
                </div>
                <p className="text-sm text-slate-700">{c.commentText}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
