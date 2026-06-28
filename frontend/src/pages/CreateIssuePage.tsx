import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import type { CreateIssueRequest, IssueCategory } from '../types';
import { CATEGORY_LABELS } from '../components/badges';
import { ErrorBanner, Spinner } from '../components/ui';

const CATEGORIES: IssueCategory[] = [
  'ROAD',
  'WATER',
  'ELECTRICITY',
  'GARBAGE',
  'SEWAGE',
  'OTHER',
];

export default function CreateIssuePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<CreateIssueRequest>({
    title: '',
    description: '',
    latitude: null,
    longitude: null,
    category: 'ROAD',
    city: '',
    area: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  function update<K extends keyof CreateIssueRequest>(
    key: K,
    value: CreateIssueRequest[K],
  ) {
    setForm((f) => ({ ...f, [key]: value }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const created = await api.createIssue(form);
      navigate(`/issues/${created.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create issue');
    } finally {
      setLoading(false);
    }
  }

  function useMyLocation() {
    if (!navigator.geolocation) {
      setError('Geolocation not supported by your browser');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        update('latitude', pos.coords.latitude);
        update('longitude', pos.coords.longitude);
      },
      () => setError('Could not get your location'),
    );
  }

  return (
    <div className="mx-auto max-w-2xl px-4 py-6 sm:px-6">
      <h1 className="mb-6 text-2xl font-bold text-slate-900">Report an Issue</h1>
      <form onSubmit={handleSubmit} className="card space-y-4 p-6">
        {error && <ErrorBanner message={error} />}
        <div>
          <label className="label">Title</label>
          <input
            required
            value={form.title}
            onChange={(e) => update('title', e.target.value)}
            className="input"
            placeholder="Brief title of the issue"
          />
        </div>
        <div>
          <label className="label">Description</label>
          <textarea
            required
            rows={4}
            value={form.description}
            onChange={(e) => update('description', e.target.value)}
            className="input"
            placeholder="Describe the issue in detail"
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="label">Category</label>
            <select
              value={form.category}
              onChange={(e) => update('category', e.target.value as IssueCategory)}
              className="input"
            >
              {CATEGORIES.map((c) => (
                <option key={c} value={c}>
                  {CATEGORY_LABELS[c]}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">City</label>
            <input
              required
              value={form.city}
              onChange={(e) => update('city', e.target.value)}
              className="input"
              placeholder="e.g. Mumbai"
            />
          </div>
        </div>
        <div>
          <label className="label">Area</label>
          <input
            required
            value={form.area}
            onChange={(e) => update('area', e.target.value)}
            className="input"
            placeholder="e.g. Andheri West"
          />
        </div>
        <div className="grid gap-4 sm:grid-cols-2">
          <div>
            <label className="label">Latitude</label>
            <input
              type="number"
              step="any"
              value={form.latitude ?? ''}
              onChange={(e) =>
                update('latitude', e.target.value ? Number(e.target.value) : null)
              }
              className="input"
              placeholder="Optional"
            />
          </div>
          <div>
            <label className="label">Longitude</label>
            <input
              type="number"
              step="any"
              value={form.longitude ?? ''}
              onChange={(e) =>
                update('longitude', e.target.value ? Number(e.target.value) : null)
              }
              className="input"
              placeholder="Optional"
            />
          </div>
        </div>
        <button
          type="button"
          onClick={useMyLocation}
          className="btn-secondary text-xs"
        >
          Use my current location
        </button>
        <div className="flex justify-end gap-3 pt-2">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="btn-secondary"
          >
            Cancel
          </button>
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? <Spinner /> : 'Submit Issue'}
          </button>
        </div>
      </form>
    </div>
  );
}
