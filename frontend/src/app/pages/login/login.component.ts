import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

/**
 * Login form where user provides userId and password.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  private authService = inject(AuthService);
  private router = inject(Router);

  userId = '';
  password = '';
  error: string | null = null;

  submit(): void {
    this.error = null;

    this.authService.login(this.userId, this.password)
      .subscribe({
        next: () => this.router.navigate(['/chat']),
        error: () => this.error = 'Invalid credentials'
      });
  }

}
