import { inject } from "@angular/core";
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptorFn,
  HttpRequest,
  HttpErrorResponse,
} from "@angular/common/http";
import { Router } from "@angular/router";
import { catchError, throwError } from "rxjs";

export const InvalidTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 403 || error.status === 401) {
        // Optional: clear token or session
        localStorage.removeItem("token");

        // Navigate to login page
        router.navigate(["auth"]);
      }
      return throwError(() => error);
    }),
  );
};
