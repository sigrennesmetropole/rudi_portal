import {AfterViewInit, Component, ContentChild, ContentChildren, QueryList, ViewChild, ViewContainerRef} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {TabContentDirective} from '../tab-content.directive';
import {TabComponent} from '../tab/tab.component';
import {TabsLayoutDirective} from '../tabs-layout.directive';
import {WorkInProgressComponent} from '../work-in-progress/work-in-progress.component';

@Component({
    selector: 'app-tabs',
    templateUrl: './tabs.component.html',
    styleUrls: ['./tabs.component.scss']
})
export class TabsComponent implements AfterViewInit {

    @ContentChildren(TabComponent)
    tabs: QueryList<TabComponent>;

    @ViewChild('defaultLayoutTabContent', {read: ViewContainerRef})
    defaultLayoutTabContent: ViewContainerRef;

    /**
     * Layout du contenu des onglets sous la bannière app-banner.
     * Si absent, alors le contenu des onglets prend toute la place sous la bannière.
     */
    @ContentChild(TabsLayoutDirective)
    customLayout: TabsLayoutDirective;

    /**
     * Contenu de l'onglet sélectionné, lorsqu'on a spécifié un {@link customLayout}.
     */
    @ContentChild(TabContentDirective)
    customLayoutTabContent: TabContentDirective;
    mediaSize: MediaSize;

    get selectedTab(): TabComponent {
        return this.tabs
            .find((tab) => tab.active);
    }

    constructor(private readonly breakpointObserverService: BreakpointObserverService,
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }

    private static displayTabInto(tab: TabComponent, viewContainer: ViewContainerRef): void {
        viewContainer.clear();
        viewContainer.createEmbeddedView(tab.templateRef);
    }

    ngAfterViewInit(): void {
        if (!this.selectedTab) {
            // setTimeout pour éviter l'erreur ExpressionChangedAfterItHasBeenCheckedError
            // tslint:disable-next-line:max-line-length
            // source : https://indepth.dev/posts/1001/everything-you-need-to-know-about-the-expressionchangedafterithasbeencheckederror-error#asynchronous-update)
            setTimeout(() => {
                this.selectTab(this.tabs.first);
            });
        }
    }

    selectTab(tabToSelect: TabComponent): void {
        if (this.isSelectable(tabToSelect)) {
            this.tabs.toArray().forEach(tab => tab.active = false);
            tabToSelect.active = true;
            this.displayTab(tabToSelect);
        }
    }

    private isSelectable(tabToSelect: TabComponent): boolean {
        return tabToSelect !== this.selectedTab && !tabToSelect.disabled;
    }

    private get viewContainerRef(): ViewContainerRef {
        if (this.customLayout) {
            if (this.customLayoutTabContent) {
                if (this.customLayoutTabContent.viewContainer) {
                    return this.customLayoutTabContent.viewContainer;
                }
            } else {
                throw new Error(`Cannot find <ng-container appTabContent> child element in <ng-container appTabsLayout>`);
            }
        }
        return this.defaultLayoutTabContent;
    }

    private displayTab(tab: TabComponent): void {
        if (tab.templateRef) {
            TabsComponent.displayTabInto(tab, this.viewContainerRef);
        } else { // On crée et affiche une instance de work-in-progress quand on n'a pas de composant spécifique à afficher
            this.displayWorkInProgressIntoCurrentTab();
        }
    }

    private displayWorkInProgressIntoCurrentTab() {
        this.viewContainerRef.clear();
        this.viewContainerRef.createComponent(WorkInProgressComponent);
    }
}
