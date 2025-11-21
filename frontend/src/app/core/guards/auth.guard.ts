import { inject } from '@angular/core';
import {
  CanActivateFn,
  Router
} from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs/operators';

/**
 * Route guard protecting /chat route.
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.authState$
    .pipe(
      map(state => {
        if (state && state.token) {
          return true;
        }
        router.navigate(['/login']);
        return false;
      })
    );
};
