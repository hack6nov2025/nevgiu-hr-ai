import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthService } from './core/services/auth.service';

/**
 * Root component with header and router outlet.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private authService = inject(AuthService);
  private router = inject(Router);

  isAuthenticated$ = this.authService
    .authState$
    .pipe(map(state => !!state?.token));

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
