import { Component, inject, OnInit } from '@angular/core';
import { ChatService } from '../../core/services/chat.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Chat component with conversation list and messages.
 */
@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {

  private chatService = inject(ChatService);

  conversations: any[] = [];
  messages: any[] = [];
  selectedConversationId: number | null = null;

  inputMessage = '';
  loading = false;

  ngOnInit(): void {
    this.loadConversations();
  }

  loadConversations(): void {
    this.chatService.getConversations()
      .subscribe((convs: any) => {
        this.conversations = convs;
      });
  }

  selectConversation(conv: any): void {
    this.selectedConversationId = conv.id;
    this.chatService.getHistory(conv.id)
      .subscribe((messages: any) => {
        this.messages = messages;
      });
  }

  send(): void {
    if (!this.inputMessage.trim()) {
      return;
    }

    this.loading = true;

    this.chatService.sendMessage(
      this.selectedConversationId,
      this.inputMessage
    )
    .subscribe((assistantMessage: any) => {
      this.messages.push({
        id: Date.now(),
        sender: 'USER',
        content: this.inputMessage,
        createdAt: new Date()
      });
      this.messages.push(assistantMessage);
      this.inputMessage = '';
      this.loading = false;

      if (!this.selectedConversationId) {
        this.loadConversations();
      }
    });
  }

}
