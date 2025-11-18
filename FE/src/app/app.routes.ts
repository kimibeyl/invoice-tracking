import { Routes } from '@angular/router';
import {authGuard} from '~/guards/AuthGuard';
import {authenticationRoutes} from '~/authentication/authentication.routes';

export const routes: Routes = [
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./layout/layout.component').then((c) => c.LayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () => import('./account-holder/account-holder.component').then(m => m.AccountHolderComponent)
      },
      {
        path: ':id/invoices',
        loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'notifications',
        loadComponent: () => import('./notification/notification.component').then(m => m.NotificationComponent)
      }
    ],
  },
  ...authenticationRoutes,
]
