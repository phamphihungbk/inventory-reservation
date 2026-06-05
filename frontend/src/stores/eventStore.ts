import { defineStore } from 'pinia';

import { fetchEvent, fetchEvents, searchEvents } from '@/api/events';
import { toFriendlyError } from '@/api/http';
import type { Event, EventSearchResult, Page } from '@/types/event';

interface EventState {
  events: Event[];
  selectedEvent: Event | null;
  searchResults: Page<EventSearchResult> | null;
  isLoading: boolean;
  error: string | null;
}

export const useEventStore = defineStore('eventStore', {
  state: (): EventState => ({
    events: [],
    selectedEvent: null,
    searchResults: null,
    isLoading: false,
    error: null,
  }),
  actions: {
    async loadEvents() {
      this.isLoading = true;
      this.error = null;
      try {
        this.events = await fetchEvents();
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },

    async loadEvent(id: number) {
      this.isLoading = true;
      this.error = null;
      try {
        this.selectedEvent = await fetchEvent(id);
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },

    async search(query: string, page = 0) {
      this.isLoading = true;
      this.error = null;
      try {
        this.searchResults = await searchEvents(query, page);
      } catch (error) {
        this.error = toFriendlyError(error);
      } finally {
        this.isLoading = false;
      }
    },

    applyInventoryUpdate(ticketTypeId: number, remainingQuantity: number) {
      const update = (event: Event) => ({
        ...event,
        ticketTypes: event.ticketTypes.map((ticketType) =>
          ticketType.id === ticketTypeId ? { ...ticketType, remainingQuantity } : ticketType,
        ),
      });
      this.events = this.events.map(update);
      this.selectedEvent = this.selectedEvent ? update(this.selectedEvent) : null;
    },
  },
});
