import { useEffect, useState } from 'react';
import { MapContainer, Marker, Popup, TileLayer } from 'react-leaflet';
import L from 'leaflet';
import { api } from '../api/client';
import type { MapIssueDTO } from '../types';
import { ErrorBanner, PageContainer, Spinner } from '../components/ui';

delete (L.Icon.Default.prototype as unknown as { _getIconUrl?: unknown })
  ._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

const STATUS_COLORS: Record<string, string> = {
  REPORTED: '#64748b',
  UNDER_REVIEW: '#6366f1',
  IN_PROGRESS: '#2563eb',
  RESOLVED: '#059669',
};

function coloredIcon(color: string) {
  return L.divIcon({
    className: 'custom-marker',
    html: `<div style="background:${color};width:14px;height:14px;border-radius:50%;border:2px solid white;box-shadow:0 1px 3px rgba(0,0,0,0.4)"></div>`,
    iconSize: [18, 18],
    iconAnchor: [9, 9],
  });
}

export default function SuperAdminMapPage() {
  const [issues, setIssues] = useState<MapIssueDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    api
      .getMapIssues()
      .then(setIssues)
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, []);

  const withCoords = issues.filter(
    (i) => i.latitude != null && i.longitude != null,
  );

  return (
    <PageContainer
      title="Issues Map"
      subtitle="Geographic view of all reported issues"
    >
      {error && <ErrorBanner message={error} />}
      {loading ? (
        <div className="grid place-items-center py-20">
          <Spinner className="h-8 w-8" />
        </div>
      ) : (
        <>
          <div className="card mb-4 h-[600px] w-full overflow-hidden">
            {withCoords.length === 0 ? (
              <div className="grid h-full place-items-center text-slate-400">
                No issues with coordinates available.
              </div>
            ) : (
              <MapContainer
                center={[withCoords[0].latitude!, withCoords[0].longitude!]}
                zoom={11}
                scrollWheelZoom
              >
                <TileLayer
                  attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {withCoords.map((i) => (
                  <Marker
                    key={i.id}
                    position={[i.latitude!, i.longitude!]}
                    icon={coloredIcon(STATUS_COLORS[i.status] ?? '#64748b')}
                  >
                    <Popup>
                      <div>
                        <strong>#{i.id}</strong> — {i.category}
                        <br />
                        Status: {i.status}
                        {i.aiSeverity && (
                          <>
                            <br />
                            Severity: {i.aiSeverity}
                          </>
                        )}
                      </div>
                    </Popup>
                  </Marker>
                ))}
              </MapContainer>
            )}
          </div>
          <div className="flex flex-wrap gap-4 text-sm">
            {Object.entries(STATUS_COLORS).map(([status, color]) => (
              <div key={status} className="flex items-center gap-2">
                <span
                  className="h-3 w-3 rounded-full"
                  style={{ background: color }}
                />
                <span className="text-slate-600">{status}</span>
              </div>
            ))}
          </div>
        </>
      )}
    </PageContainer>
  );
}
