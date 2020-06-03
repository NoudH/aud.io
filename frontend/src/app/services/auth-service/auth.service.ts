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

    return this.http.post('http://api-audio.us-east-1.elasticbeanstalk.com/api/auth/login', accountObj, {responseType: 'text'});
  }

  postSignUp(username: string, email: string, password: string) {
    const accountObj = {
      username,
      email,
      password
    };

    return this.http.post('http://api-audio.us-east-1.elasticbeanstalk.com/api/auth/sign-up', accountObj);
  }

  putActivate(token: string) {
    return this.http.put('http://api-audio.us-east-1.elasticbeanstalk.com/api/auth/activate', null, {headers: {Authorization: 'Bearer ' + token}});
  }
}
