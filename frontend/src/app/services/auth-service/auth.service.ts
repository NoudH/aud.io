import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';

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

    return this.http.post('http://localhost:8762/api/auth/login', accountObj);
  }

  postSignUp(username: string, email: string, password: string) {
    const accountObj = {
      username,
      email,
      password
    };

    return this.http.post('http://localhost:8762/api/auth/sign-up', accountObj);
  }
}
