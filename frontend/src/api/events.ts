import { apiClient } from '@/api/http';
import type { Event, EventSearchResult, Page } from '@/types/event';

export async function fetchEvents(): Promise<Event[]> {
  const { data } = await apiClient.get<Event[]>('/events');
  return data;
}

export async function fetchEvent(id: number): Promise<Event> {
  const { data } = await apiClient.get<Event>(`/events/${id}`);
  return data;
}

export async function searchEvents(query: string, page = 0, size = 10): Promise<Page<EventSearchResult>> {
  const { data } = await apiClient.get<Page<EventSearchResult>>('/events/search', {
    params: {
      q: query,
      page,
      size,
    },
  });
  return data;
}
