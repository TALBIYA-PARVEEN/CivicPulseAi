import type { IssueCategory, IssueStatus, Priority } from '../types';

export const CATEGORY_LABELS: Record<IssueCategory, string> = {
  ROAD: 'Road',
  WATER: 'Water',
  ELECTRICITY: 'Electricity',
  GARBAGE: 'Garbage',
  SEWAGE: 'Sewage',
  OTHER: 'Other',
};

export const CATEGORY_COLORS: Record<IssueCategory, string> = {
  ROAD: 'bg-amber-100 text-amber-800',
  WATER: 'bg-sky-100 text-sky-800',
  ELECTRICITY: 'bg-yellow-100 text-yellow-800',
  GARBAGE: 'bg-emerald-100 text-emerald-800',
  SEWAGE: 'bg-stone-200 text-stone-800',
  OTHER: 'bg-slate-100 text-slate-700',
};

export const STATUS_LABELS: Record<IssueStatus, string> = {
  REPORTED: 'Reported',
  UNDER_REVIEW: 'Under Review',
  IN_PROGRESS: 'In Progress',
  RESOLVED: 'Resolved',
};

export const STATUS_COLORS: Record<IssueStatus, string> = {
  REPORTED: 'bg-slate-100 text-slate-700',
  UNDER_REVIEW: 'bg-indigo-100 text-indigo-700',
  IN_PROGRESS: 'bg-blue-100 text-blue-700',
  RESOLVED: 'bg-emerald-100 text-emerald-700',
};

export const PRIORITY_COLORS: Record<Priority, string> = {
  LOW: 'bg-slate-100 text-slate-700',
  MEDIUM: 'bg-amber-100 text-amber-800',
  HIGH: 'bg-orange-100 text-orange-800',
  CRITICAL: 'bg-red-100 text-red-800',
};

export function formatDate(iso: string): string {
  try {
    return new Date(iso).toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return iso;
  }
}
