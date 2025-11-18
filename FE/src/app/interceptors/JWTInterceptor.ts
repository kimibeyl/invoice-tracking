import { HttpInterceptorFn } from '@angular/common/http';

export const JwtIntercept: HttpInterceptorFn = (req, next) => {

  console.log('Interceptor called'); // For debugging

  const token = localStorage.getItem('token');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }
console.log(req)
  return next(req);
};
