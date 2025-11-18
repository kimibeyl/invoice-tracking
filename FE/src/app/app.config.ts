import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptors} from '@angular/common/http';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {MyPreset} from '~/my-preset';
import {JwtIntercept} from '~/interceptors/JWTInterceptor';
import {InvalidTokenInterceptor} from '~/interceptors/InvalidTokenInterceptor';
import {invoiceModule} from '~/api/invoice.module';
import {environment} from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors([JwtIntercept, InvalidTokenInterceptor])
    ),
    provideHttpClient(), provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes),
    providePrimeNG({
      theme: {
        preset: MyPreset,
        options: {
          darkModeSelector: 'none'
        }
      }
    }),
    importProvidersFrom([
      invoiceModule.forRoot({ rootUrl: environment.apiUrl })
    ])
  ]
};
