import { useEffect, useState } from 'react';
import { api } from '../api/client';
import type { NotificationResponse } from '../types';
import { formatDate } from '../components/badges';
import { EmptyState, ErrorBanner, PageContainer, Spinner } from '../components/ui';

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<NotificationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .getNotifications()
      .then(setNotifications)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  return (
    <PageContainer title="Notifications" subtitle="Your recent alerts and updates">
      {error && <ErrorBanner message={error} />}
      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : notifications.length === 0 ? (
        <EmptyState message="No notifications yet." />
      ) : (
        <div className="space-y-3">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`card flex items-start gap-3 p-4 ${
                !n.isRead ? 'border-l-4 border-l-primary-500' : ''
              }`}
            >
              <div
                className={`mt-1 h-2.5 w-2.5 flex-shrink-0 rounded-full ${
                  n.isRead ? 'bg-slate-300' : 'bg-primary-500'
                }`}
              />
              <div className="flex-1">
                <p className="text-sm text-slate-800">{n.message}</p>
                <p className="mt-1 text-xs text-slate-400">
                  {formatDate(n.createdAt)}
                </p>
              </div>
            </div>
          ))}
        </div>
      )}
    </PageContainer>
  );
}
