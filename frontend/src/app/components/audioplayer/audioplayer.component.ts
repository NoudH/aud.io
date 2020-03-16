import { Component, OnInit } from '@angular/core';
import {AudioObject} from '../../objects/audio-object';

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
  currentTime: number;
  duration: number;
  trackTitle: string;
  isPlaying = true;
  volumeIcon = 'volume_up';
  currentRepeatMode = this.repeatModes.REPEAT;

  ngOnInit(): void {
    this.trackTitle = 'Track Title';
    this.audioObject.audio.src = 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3';
    this.audioObject.play();

    this.audioObject.audio.addEventListener('timeupdate', (event) => {
      this.currentTime = (event.target as HTMLAudioElement).currentTime;
    });

    this.audioObject.audio.addEventListener( 'loadedmetadata', (metadata) => {
      this.duration = (metadata.target as HTMLAudioElement).duration;
    });
  }

  formatLabel(value: number | null) {
    if (!value) {
      return '0:00';
    }
    const minutes = Math.floor(value / 60);
    const seconds = Math.floor(value % 60);

    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
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
