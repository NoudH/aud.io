import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Visibility} from '../../domain/visibility.enum';
import {BaseUrl} from '../url-service/base-url';

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
    return this.http.get<any>(`${BaseUrl.url}/api/audio/search`, {params});
  }

  public postUploadTrack(name: string, visibility: Visibility, file: File) {
    const infoObj = {name, visibility: Visibility[visibility]};
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(`${BaseUrl.url}/api/audio/`, formData, {
      params: new HttpParams().set('track', JSON.stringify(infoObj))
    });
  }
}
