import {Track} from '../../domain/track';

export class AudioObject {

  private static instance: AudioObject;

  audio = new Audio();

  private onTrackChangeSubs: ((track: Track) => void)[] = [];

  private constructor() {}

  public static getInstance() {
    if (!this.instance) {
      this.instance = new AudioObject();
    }
    return this.instance;
  }

  public play() {
    this.audio.play();
  }

  public pause() {
    this.audio.pause();
  }

  public changeTrack(track: Track) {
    this.onTrackChangeSubs.forEach(x => {
      x(track);
    });
  }

  public onTrackChange(listener: (track: Track) => void) {
    this.onTrackChangeSubs.push(listener);
  }
}
