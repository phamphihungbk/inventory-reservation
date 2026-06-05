import { apiClient } from '@/api/http';
import type { NotificationDebug } from '@/types/notification';

export async function fetchNotifications(): Promise<NotificationDebug[]> {
  const { data } = await apiClient.get<NotificationDebug[]>('/admin/notifications');
  return data;
}
