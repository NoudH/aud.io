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

  repeatModes = {
    REPEAT: 'repeat',
    REPEAT_ONE: 'repeat_one',
    SHUFFLE: 'shuffle'
  };

  audioObject = AudioObject.getInstance();
  currentTime = 0;
  duration = 0;
  trackTitle: string;
  isPlaying = true;
  volumeIcon = 'volume_up';
  currentRepeatMode = this.repeatModes.REPEAT;

  ngOnInit(): void {

    this.audioObject.audio.addEventListener('timeupdate', (event) => {
      this.currentTime = (event.target as HTMLAudioElement).currentTime;
    });

    this.audioObject.audio.addEventListener( 'loadedmetadata', (metadata) => {
      this.duration = (metadata.target as HTMLAudioElement).duration;
    });

    this.audioObject.onTrackChange((track: Track) => {
      this.trackTitle = track.name;
      this.duration = 0;
      this.audioObject.audio.src = track.source;
      this.audioObject.play();
      this.isPlaying = true;
    });
  }

  sliderChange(event) {
    this.audioObject.audio.currentTime = event.value;
  }

  sliderStartDrag() {
    this.audioObject.pause();
    this.isPlaying = false;
  }

  sliderStopDrag() {
    this.audioObject.play();
    this.isPlaying = true;
  }

  sliderIsDragging(event) {
    this.currentTime = event.value;
  }

  togglePlaying() {
    this.isPlaying = !this.isPlaying;

    this.isPlaying ? this.audioObject.play() : this.audioObject.pause();
  }

  volumeChange(event) {
    this.audioObject.audio.volume = (event.value / 100);
    this.volumeIcon =
      event.value > 50 ? 'volume_up' :
      event.value !== 0 ? 'volume_down' : 'volume_off';
  }

  toggleRepeat() {
    switch (this.currentRepeatMode) {
      case this.repeatModes.REPEAT:
        this.currentRepeatMode = this.repeatModes.REPEAT_ONE;
        break;
      case this.repeatModes.REPEAT_ONE:
        this.currentRepeatMode = this.repeatModes.SHUFFLE;
        break;
      case this.repeatModes.SHUFFLE:
        this.currentRepeatMode = this.repeatModes.REPEAT;
        break;
    }
  }
}
