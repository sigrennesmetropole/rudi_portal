import {AfterViewInit, Component, ContentChild, ContentChildren, QueryList, ViewChild, ViewContainerRef} from '@angular/core';
import {TabComponent} from '../tab/tab.component';
import {TabsLayoutDirective} from '../tabs-layout.directive';
import {TabContentDirective} from '../tab-content.directive';

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

    get selectedTab(): TabComponent {
        return this.tabs
            .find((tab) => tab.active);
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
        return tabToSelect !== this.selectedTab && !tabToSelect.disabled && !!tabToSelect.templateRef;
    }

    private displayTab(tab: TabComponent): void {
        if (tab.templateRef) {
            if (this.customLayout) {
                if (this.customLayoutTabContent) {
                    if (this.customLayoutTabContent.viewContainer) {
                        TabsComponent.displayTabInto(tab, this.customLayoutTabContent.viewContainer);
                    }
                } else {
                    throw new Error(`Cannot find <ng-container appTabContent> child element in <ng-container appTabsLayout>`);
                }
            } else {
                TabsComponent.displayTabInto(tab, this.defaultLayoutTabContent);
            }
        }
    }
}
