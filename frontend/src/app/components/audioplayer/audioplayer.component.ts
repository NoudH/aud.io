import { Component, OnInit } from '@angular/core';
import {AudioObject} from '../../objects/audio-object/audio-object';
import {Track} from '../../domain/track';

@Component({
  selector: 'app-audioplayer',
  templateUrl: './audioplayer.component.html',
  styleUrls: ['./audioplayer.component.css']
})
export class AudioplayerComponent implements OnInit {

  constructor() { }

  audioObject = AudioObject.getInstance();
  duration = 0;
  trackTitle: string;
  volumeIcon = 'volume_up';

  ngOnInit(): void {

    this.audioObject.audio.addEventListener('timeupdate', (event) => {
      this.audioObject.currentTime = (event.target as HTMLAudioElement).currentTime;
    });

    this.audioObject.audio.addEventListener( 'loadedmetadata', (metadata) => {
      this.duration = (metadata.target as HTMLAudioElement).duration;
    });

    this.audioObject.onTrackChange((track: Track) => {
      this.trackTitle = track.name;
      this.duration = 0;
    });
  }

  sliderChange(event) {
    this.audioObject.audio.currentTime = event.value;
  }

  sliderStartDrag() {
    this.audioObject.pause();
  }

  sliderStopDrag() {
    this.audioObject.play();
  }

  sliderIsDragging(event) {
    this.audioObject.currentTime = event.value;
  }

  togglePlaying() {
    this.audioObject.isPlaying() ? this.audioObject.pause() : this.audioObject.play();
  }

  volumeChange(event) {
    this.audioObject.audio.volume = (event.value / 100);
    this.volumeIcon =
      event.value > 50 ? 'volume_up' :
      event.value !== 0 ? 'volume_down' : 'volume_off';
  }

  toggleRepeat() {
    switch (this.audioObject.currentRepeatMode) {
      case this.audioObject.repeatModes.REPEAT:
        this.audioObject.currentRepeatMode = this.audioObject.repeatModes.REPEAT_ONE;
        break;
      case this.audioObject.repeatModes.REPEAT_ONE:
        this.audioObject.currentRepeatMode = this.audioObject.repeatModes.SHUFFLE;
        break;
      case this.audioObject.repeatModes.SHUFFLE:
        this.audioObject.currentRepeatMode = this.audioObject.repeatModes.SINGLE;
        break;
      case this.audioObject.repeatModes.SINGLE:
        this.audioObject.currentRepeatMode = this.audioObject.repeatModes.REPEAT;
        break;
    }
  }
}
