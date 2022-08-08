import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Subscription} from 'rxjs';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import {FiltersService} from '../../../core/services/filters.service';


const EMPTY_SEARCH = '';

@Component({
  selector: 'app-search-box',
  templateUrl: './search-box.component.html',
  styleUrls: ['./search-box.component.scss']
})

export class SearchBoxComponent implements OnInit, OnDestroy {
  @Input() mediaSize: MediaSize;
  searchTerms = EMPTY_SEARCH;
  private filtersServiceSubscription?: Subscription;

  constructor(
    iconRegistry: MatIconRegistry,
    sanitizer: DomSanitizer,
    public filtersService: FiltersService,
    ) {
    iconRegistry.addSvgIcon(
      'filter-icon',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/filter-icon.svg'));
    iconRegistry.addSvgIcon(
      'search',
      sanitizer.bypassSecurityTrustResourceUrl('assets/icons/search.svg'));
  }

  ngOnInit(): void {
    this.filtersServiceSubscription = this.filtersService.searchFilter.value$.subscribe(search => {
      this.searchTerms = search;
    });
  }

  /**
   * Fonction permettant de vider le champ input de la recherche et l'initialisation de la liste
   */
  onReset(): void {
    this.searchTerms = EMPTY_SEARCH;
    this.onChanges();
  }

  /**
   * Méthode liée au déclenchement de l'event "key.enter" du champ de recherche
   */
  onChanges(): void {
    this.filtersService.searchFilter.value = this.searchTerms;
  }

  ngOnDestroy(): void {
    this.filtersServiceSubscription?.unsubscribe();
  }
}
