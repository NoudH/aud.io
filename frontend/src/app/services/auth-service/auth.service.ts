import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BaseUrl} from '../url-service/base-url';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  public postLogin(username: string, password: string) {
    const accountObj = {
      username,
      email: username,
      password
    };

    return this.http.post(`${BaseUrl.url}/api/auth/login`, accountObj, {responseType: 'text'});
  }

  postSignUp(username: string, email: string, password: string) {
    const accountObj = {
      username,
      email,
      password
    };

    return this.http.post(`${BaseUrl.url}/api/auth/sign-up`, accountObj);
  }

  putActivate(token: string) {
    return this.http.put(`${BaseUrl.url}/api/auth/activate`, null, {headers: {Authorization: 'Bearer ' + token}});
  }
}
