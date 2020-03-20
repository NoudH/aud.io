import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'minuteSeconds'
})
export class MinuteSecondsPipe implements PipeTransform {

  transform(value: number | string | null): string {
    if (!value) {
      return '0:00';

    } else if (typeof value === 'number') {

      const minutes = Math.floor(value / 60);
      const seconds = Math.floor(value % 60);

      return `${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;

    } else if (typeof value === 'string') {

      return value;
    }
  }
}
