import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TrackService {

  constructor(private http: HttpClient) { }

  public getSearchTracks(title: string, page: number): Observable<any> {
    const params = new HttpParams()
      .append('title', title)
      .append('page', page.toString())
      .append('size', '20');
    return this.http.get<any>('http://localhost:8762/api/audio/search', {params});
  }
}
