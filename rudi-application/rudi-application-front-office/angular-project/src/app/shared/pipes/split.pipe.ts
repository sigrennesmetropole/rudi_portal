import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'split'
})
export class SplitPipe implements PipeTransform {

  transform(value: string[], separator: string): string {
    const splits: string[] = value.slice(1, value.length);
    if (splits.length > 1) {
      return splits.join(separator);
    } else {
      return '';
    }
  }
}
