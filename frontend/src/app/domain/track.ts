export class Track {

  id: number;
  name: string;
  uploader: any;
  audioUrl: string;
  length: number;

  constructor(id: number, name: string, uploader: any, audioUrl: string, length: number) {
    this.id = id;
    this.name = name;
    this.uploader = uploader;
    this.audioUrl = audioUrl;
    this.length = length;
  }
}
