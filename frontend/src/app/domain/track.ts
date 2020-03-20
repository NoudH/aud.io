export class Track {

  id: number;
  name: string;
  artist: string;
  source: string;
  length: number;

  constructor(id: number, name: string, artist: string, source: string, length: number) {
    this.id = id;
    this.name = name;
    this.artist = artist;
    this.source = source;
    this.length = length;
  }
}
