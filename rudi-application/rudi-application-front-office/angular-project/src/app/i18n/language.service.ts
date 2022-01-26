import {Injectable} from '@angular/core';
import {DictionaryEntry, Language} from "../api-kaccess";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  constructor() {
  }

  getCurrentLanguage(): Language {
    return Language.FrFr;
  }

  getTextForCurrentLanguage(entries: DictionaryEntry[]): string | undefined {
    if (!entries) {
      return undefined
    }

    const entryForCurrentLanguage = this.getEntryForCurrentLanguage(entries);
    if (entryForCurrentLanguage) {
      return entryForCurrentLanguage.text
    } else {
      console.error(`No entry matching ${this.getCurrentLanguage()} among entries :`, entries)
      return undefined
    }
  }

  private getEntryForCurrentLanguage(entries: DictionaryEntry[]): DictionaryEntry | undefined {
    if (!entries || entries.length == 0) {
      return undefined
    }

    if (entries.length > 1) {
      const currentLanguage = this.getCurrentLanguage()
      for (const entry of entries) {
        if (entry.lang === currentLanguage) {
          return entry
        }
      }
    }

    return entries[0]
  }

}
