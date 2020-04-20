import {Component, ViewChild} from '@angular/core';
import {TrackService} from './services/track-service/track.service';
import {AudiolistComponent} from './components/audiolist/audiolist.component';
import {Track} from './domain/track';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {LoginFormComponent} from './components/login-form/login-form.component';
import {UploadFormComponent} from './components/upload-form/upload-form.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor() {}
}
