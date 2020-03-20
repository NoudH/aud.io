import { Component, OnInit } from '@angular/core';
import {Track} from '../../domain/track';
import {AudioObject} from '../../objects/audio-object/audio-object';

@Component({
  selector: 'app-audiolist',
  templateUrl: './audiolist.component.html',
  styleUrls: ['./audiolist.component.css']
})
export class AudiolistComponent implements OnInit {

  private audioObject = AudioObject.getInstance();

  constructor() { }

  searchResults = [
    new Track(1, 'Track name 1', 'Artist', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 372),
    new Track(2, 'Track name 2', 'Artist', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 372),
    new Track(3, 'Track name 3', 'Artist', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 372),
  ];

  ngOnInit(): void {
  }

  playTrack(track: Track) {
    this.audioObject.changeTrack(track);
  }
}
