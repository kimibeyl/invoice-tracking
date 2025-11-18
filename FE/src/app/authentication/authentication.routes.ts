import { Routes } from '@angular/router';

export const authenticationRoutes: Routes = [
  {
    path: 'auth',
    loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)
  }
]
