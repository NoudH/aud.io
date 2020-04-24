import { Track } from './track';

describe('Track', () => {
  it('should create an instance', () => {
    expect(new Track(0, 'test', {test: 'test'}, 'test', 1)).toBeTruthy();
  });
});
