import {inject, ViewContainerRef} from '@angular/core';
import {TabContentDirective} from './tab-content.directive';

describe('TabContentDirective', () => {
  it('should create an instance', () => {
    const viewContainerRef = inject(ViewContainerRef);
    const directive = new TabContentDirective(viewContainerRef);
    expect(directive).toBeTruthy();
  });
});
