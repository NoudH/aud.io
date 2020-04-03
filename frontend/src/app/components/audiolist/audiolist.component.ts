import { Component, OnInit } from '@angular/core';
import {Track} from '../../domain/track';
import {AudioObject} from '../../objects/audio-object/audio-object';

@Component({
  selector: 'app-audiolist',
  templateUrl: './audiolist.component.html',
  styleUrls: ['./audiolist.component.css']
})
export class AudiolistComponent implements OnInit {

  audioObject = AudioObject.getInstance();

  constructor() { }

  public searchResults = [];

  ngOnInit(): void {
  }

  playTrack(track: Track) {
    this.audioObject.playNow(track);
  }
}
