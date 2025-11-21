import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * Chat service calling backend endpoints.
 */
@Injectable({ providedIn: 'root' })
export class ChatService {

  // TODO: change to http://<PUBLIC_IP>:8080/api/chat in prod
  private baseUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) {}

  sendMessage(conversationId: number | null, message: string) {
    return this.http.post(
      `${this.baseUrl}/send`,
      { conversationId, message }
    );
  }

  getHistory(conversationId: number) {
    return this.http.get(
      `${this.baseUrl}/history/${conversationId}`
    );
  }

  getConversations() {
    return this.http.get(
      `${this.baseUrl}/conversations`
    );
  }

}
