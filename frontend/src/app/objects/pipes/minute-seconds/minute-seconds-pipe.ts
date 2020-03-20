import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'minuteSeconds'
})
export class MinuteSecondsPipe implements PipeTransform {

  transform(value: number): string {
    if (!value) {
      return '0:00';
    }
    const minutes = Math.floor(value / 60);
    const seconds = Math.floor(value % 60);

    return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  }
}
