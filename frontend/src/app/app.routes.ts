import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ChatComponent } from './pages/chat/chat.component';
import { authGuard } from './core/guards/auth.guard';

/**
 * Application routes.
 */
export const APP_ROUTES: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'chat',
    component: ChatComponent,
    canActivate: [authGuard]
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
