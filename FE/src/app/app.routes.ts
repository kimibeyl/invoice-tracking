import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
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
    ]
  }]
