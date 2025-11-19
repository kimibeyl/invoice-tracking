import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isAuthenticated = !!localStorage.getItem("token");

  if (isAuthenticated) {
    return true;
  } else {
    return router.createUrlTree(["/auth"]);
  }
};
