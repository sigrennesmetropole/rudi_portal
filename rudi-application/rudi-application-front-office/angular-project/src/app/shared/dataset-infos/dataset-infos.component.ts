import {Component, Input, OnInit} from '@angular/core';
import {BreakpointObserverService, MediaSize} from '../../core/services/breakpoint-observer.service';
import {IconRegistryService} from '../../core/services/icon-registry.service';
import {ALL_TYPES} from '../models/title-icon-type';
import {Router} from '@angular/router';

@Component({
    selector: 'app-dataset-infos',
    templateUrl: './dataset-infos.component.html',
    styleUrls: ['./dataset-infos.component.scss']
})
export class DatasetsInfosComponent implements OnInit {
    /**
     * affiche ou pas  le logo
     */
    @Input() showLogo: boolean;
    /**
     * le nom de l'organisation
     */
    @Input() organizationName: string;
    /**
     * Le titre du dataset
     */
    @Input() resourceTitle: string;
    /**
     *  le jdd est restreint ou non ?
     */
    @Input() isRestricted: boolean;
    /**
     * l'id de l'organisation
     */
    @Input() organizationId: string;
    /**
     * Ajoute un diviseur sous chaque élément de la card. Par défaut : false.
     */
    @Input() divider = false;
    /**
     * le lien vers le détail du jdd courrant? Par défaut : false.
     */
    @Input() goToDetails = false;
    /**
     * Id du jdd courrant
     *
     */
    @Input() currentJddId: string;
    restrictedDatasetIcon = 'key_icon_88_secondary-color';
    mediaSize: MediaSize;


    constructor(
        private readonly breakpointObserverService: BreakpointObserverService,
        private router: Router,
        iconRegistryService: IconRegistryService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
        this.mediaSize = this.breakpointObserverService.getMediaSize();
    }

    ngOnInit(): void {
    }

    /**
     * Méthode appelée au clic sur un jdd et qui redirige l'utilisateur vers la page de details de ce jdd
     * @param currentJddId
     */
    handleClickOnDatasetCard(currentJddId: string): void {
        if (currentJddId) {
            this.router.navigate(['/catalogue/detail/' + currentJddId]);
        }
    }
}
