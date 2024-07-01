import {HttpClient} from '@angular/common/http';
import {Injectable, NgZone} from '@angular/core';

@Injectable()
export class CaptchaHelperService {

  constructor(
    private http: HttpClient,
    private ngZone: NgZone
  ) { }

  // get script and execute it immediately
  getScript(url: string): void {
    this.http.get(url, { responseType: 'text' })
      .subscribe(
        scriptString => {
          const f = new Function(scriptString);
          this.ngZone.runOutsideAngular(() => {
            f();
          });
        }
      );
  }

  useUserInputBlurValidation(userInput: any): boolean {
    return (userInput.getAttribute('correctCaptcha') !== null);
  }

  // get captcha endpoint handler from configued captchaEndpoint value,
  // the result can be "simple-captcha-endpoint.ashx", "simple-captcha-endpoint",
  // or "simple-botdetect.php"
  getCaptchaEndpointHandler(captchaEndpoint: string): string {
    const splited = captchaEndpoint.split('/');
    return splited[splited.length - 1];
  }

  // get backend base url from configued captchaEndpoint value
  getBackendBaseUrl(captchaEndpoint: string, captchaEndpointHandler: string): string {
    const lastIndex = captchaEndpoint.lastIndexOf(captchaEndpointHandler);
    return captchaEndpoint.substring(0, lastIndex);
  }

  // change relative to absolute urls in captcha html markup
  changeRelativeToAbsoluteUrls(originCaptchaHtml: string, captchaEndpoint: string): string {
    const captchaEndpointHandler = this.getCaptchaEndpointHandler(captchaEndpoint);
    const backendUrl = this.getBackendBaseUrl(captchaEndpoint, captchaEndpointHandler);

    originCaptchaHtml = originCaptchaHtml.replace(/<script.*<\/script>/g, '');
    const relativeUrls = originCaptchaHtml.match(/(src|href)=\"([^"]+)\"/g);

      let relativeUrl, relativeUrlPrefixPattern, absoluteUrl,
          changedCaptchaHtml = originCaptchaHtml;

      for (let i = 0; i < relativeUrls.length; i++) {
          relativeUrl = relativeUrls[i].slice(0, -1).replace(/src=\"|href=\"/, '');
          relativeUrlPrefixPattern = new RegExp('.*' + captchaEndpointHandler);
          absoluteUrl = relativeUrl.replace(relativeUrlPrefixPattern, backendUrl + captchaEndpointHandler);
          changedCaptchaHtml = changedCaptchaHtml.replace(relativeUrl, absoluteUrl);
      }

    return changedCaptchaHtml;
  }

}
