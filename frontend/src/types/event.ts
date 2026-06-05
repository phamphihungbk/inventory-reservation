export interface TicketType {
  id: number;
  eventId: number;
  name: string;
  price: number;
  remainingQuantity: number;
  version: number;
}

export interface Event {
  id: number;
  name: string;
  description: string;
  venue: string;
  city: string;
  country: string;
  eventDate: string;
  ticketTypes: TicketType[];
  createdAt: string;
}

export interface EventSearchResult {
  id: number;
  name: string;
  venue: string;
  city: string;
  country: string;
  eventDate: string;
  rank: number;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
