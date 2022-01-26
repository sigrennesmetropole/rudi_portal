import {Filter} from './filter';

export abstract class ArrayFilter extends Filter<string[]> {

  remove(item: string): void {
    const index = this.indexOf(item);
    if (index !== -1) {
      const nextValue = [...this.value];
      nextValue.splice(index, 1);
      this.value = nextValue;
    }
  }

  contains(item: string): boolean {
    return this.isContainedInItems(item, this.value);
  }

  private isContainedInItems(item: string, items: string[]): boolean {
    return this.indexOfInItems(item, items) !== -1;
  }

  private indexOf(item: string): number {
    const items = this.value;
    return this.indexOfInItems(item, items);
  }

  private indexOfInItems(item: string, items: string[]): number {
    for (let i = 0; i < items.length; i++) {
      if (this.itemsAreEqual(items[i], item)) {
        return i;
      }
    }
    return -1;
  }

  get active(): boolean {
    return this.value.length > 0;
  }

  protected getEmptyValue(): string[] {
    return [];
  }

  protected itemsAreEqual(item1: string, item2: string): boolean {
    return item1 === item2;
  }

  protected valuesAreEqual(items1: string[], items2: string[]): boolean {
    if (items1.length !== items2.length) {
      return false;
    }
    for (const item1 of items1) {
      if (!this.isContainedInItems(item1, items2)) {
        return false;
      }
    }
    return true;
  }
}
