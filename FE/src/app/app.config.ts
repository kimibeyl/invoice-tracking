import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { providePrimeNG } from 'primeng/config';

import { routes } from './app.routes';
import {provideHttpClient} from '@angular/common/http';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {MyPreset} from '~/my-preset';

export const appConfig: ApplicationConfig = {
  providers: [    provideAnimationsAsync(), // Add this line to the providers array
    provideHttpClient(), provideZoneChangeDetection({ eventCoalescing: true }), provideRouter(routes),
    providePrimeNG({
      theme: {
        preset: MyPreset,
        options: {
          darkModeSelector: 'none'
        }
      }
    })]
};
