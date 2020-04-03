import {Track} from '../../domain/track';

export class AudioObject {

  private static instance: AudioObject;

  audio = new Audio();
  currentTime = 0;
  repeatModes = {
    REPEAT: 'repeat',
    REPEAT_ONE: 'repeat_one',
    SHUFFLE: 'shuffle',
    SINGLE: 'close'
  };
  currentRepeatMode = this.repeatModes.REPEAT;

  private playing = false;

  private queue: Track[];

  private queueIndex = 0;

  private onTrackChangeSubs: ((track: Track) => void)[] = [];

  private constructor() {
    this.audio.addEventListener('ended', (event) => {
      if (this.currentRepeatMode === this.repeatModes.REPEAT_ONE) {
        this.currentTime = 0;
      } else if (this.currentRepeatMode === this.repeatModes.REPEAT) {
        this.queueIndex >= this.queue.length - 1 ? this.queueIndex = 0 : this.queueIndex++;
      } else if (this.currentRepeatMode === this.repeatModes.SHUFFLE) {
        this.queueIndex = Math.round(Math.random() * (this.queue.length - 1));
      }
      if (this.currentRepeatMode !== this.repeatModes.SINGLE) {
        this.changeTrack(this.queue[this.queueIndex]);
      }
    });
  }

  public static getInstance() {
    if (!this.instance) {
      this.instance = new AudioObject();
    }
    return this.instance;
  }

  public play() {
    this.audio.play();
    this.playing = true;
  }

  public pause() {
    this.audio.pause();
    this.playing = false;
  }

  private changeTrack(track: Track) {
    this.audio.src = 'http://localhost:8762/api/audio/files/' + track.audioUrl;
    this.play();
    this.onTrackChangeSubs.forEach(x => {
      x(track);
    });
  }

  public onTrackChange(listener: (track: Track) => void) {
    this.onTrackChangeSubs.push(listener);
  }

  public isPlaying() {
    return this.playing;
  }

  public playNow(track: Track) {
    this.queue = [track];
    this.queueIndex = 0;
    this.changeTrack(track);
  }

  public nextTrackInQueue() {
    this.queueIndex >= this.queue.length - 1 ? this.queueIndex = 0 : this.queueIndex++;
    this.changeTrack(this.queue[this.queueIndex]);
  }

  public previousTrackInQueue() {
    this.queueIndex <= 0 ? this.queueIndex = this.queue.length - 1 : this.queueIndex--;
    this.changeTrack(this.queue[this.queueIndex]);
  }

  public addTrackToQueue(track: Track) {
    this.queue.push(track);
  }
}
