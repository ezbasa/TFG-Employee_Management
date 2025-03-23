import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  // Obtén el token del localStorage
  const authToken = localStorage.getItem('token');

  // Si el token existe, clona la solicitud y añade el encabezado Authorization
  if (authToken) {
    let clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });

    return next(clonedRequest);
  }

  return next(req);
};
