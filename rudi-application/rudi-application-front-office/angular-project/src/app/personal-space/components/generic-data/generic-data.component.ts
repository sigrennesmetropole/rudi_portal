import {Component, Input, OnInit} from '@angular/core';
import {DictionaryEntry} from '../../../api-kaccess';
import {GdataDataInterface} from '../../../core/services/selfdata-dataset/gdataData.interface';
import {LanguageService} from '../../../i18n/language.service';

@Component({
  selector: 'app-generic-data',
  templateUrl: './generic-data.component.html',
  styleUrls: ['./generic-data.component.scss']
})
export class GenericDataComponent implements OnInit {
    @Input() isLoading: boolean;
    @Input() genericDataObject: GdataDataInterface;

  constructor(
      private readonly languageService: LanguageService) { }

  ngOnInit(): void {
  }

    getLabel(dictionaryEntries: DictionaryEntry[]): string {
        return this.languageService.getTextForCurrentLanguage(dictionaryEntries);
    }
}
