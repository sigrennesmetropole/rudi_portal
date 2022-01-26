import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";
import {Router} from "@angular/router";
import {AuthenticationService} from "../services/authentication.service";

@Injectable()
export class ResponseTokenInterceptor implements HttpInterceptor {
  constructor(private readonly router: Router,
              private readonly authenticationService: AuthenticationService) {
  }

  /**
   * Récupération du token rénouvelé dans le header de la reponse.
   * @param req
   * @param next
   */
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse) {
          const httpResponse = event as HttpResponse<any>
          this.authenticationService.storeTokensFrom(httpResponse.headers)
        }
      })
    );
  }
}
