import {Component, OnInit, ViewChild} from '@angular/core';
import {AudiolistComponent} from '../audiolist/audiolist.component';
import {TrackService} from '../../services/track-service/track.service';
import {MatDialog} from '@angular/material/dialog';
import {Track} from '../../domain/track';
import {LoginFormComponent} from '../login-form/login-form.component';
import {UploadFormComponent} from '../upload-form/upload-form.component';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {

  title = 'frontend';
  localStorage = localStorage;

  @ViewChild('searchResults') searchList: AudiolistComponent;

  constructor(private trackService: TrackService, private dialog: MatDialog) {}

  ngOnInit(): void {}

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
