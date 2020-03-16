export class AudioObject {

  private static instance: AudioObject;

  audio = new Audio();

  private constructor() {}

  public static getInstance() {
    return this.instance ? this.instance : new AudioObject();
  }

  public play() {
    this.audio.play();
  }

  public pause() {
    this.audio.pause();
  }
}
