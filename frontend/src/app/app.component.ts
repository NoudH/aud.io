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
  title = 'frontend';

  @ViewChild('searchResults') searchList: AudiolistComponent;

  constructor(private trackService: TrackService, private dialog: MatDialog) {}

  onSearchbarChanged(val) {
    this.trackService.getSearchTracks(val.target.value, 0).subscribe((data) => {
      const tracks = [];
      data.content.forEach( x => {
        tracks.push(new Track(x.id, x.name, x.uploader, x.audioUrl.replace('files/', ''), x.duration));
      });
      this.searchList.searchResults = tracks;
    });
  }

  openLoginModal() {
    this.dialog.closeAll();
    this.dialog.open(LoginFormComponent);
  }

  openUploadModal() {
    this.dialog.closeAll();
    this.dialog.open(UploadFormComponent);
  }
}
