import { AudioObject } from './audio-object';

describe('AudioObject', () => {
  it('should create an instance', () => {
    expect(AudioObject.getInstance()).toBeTruthy();
  });
});
