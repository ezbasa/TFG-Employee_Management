import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  console.log("interceptor");

  // Obtén el token del localStorage
  const authToken = localStorage.getItem('token');

  // Si el token existe, clona la solicitud y añade el encabezado Authorization
  if (authToken) {
    let clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${authToken}`
      }
    });

    console.log("tenemos token" + authToken);

    return next(clonedRequest);
  }

  return next(req);
};
