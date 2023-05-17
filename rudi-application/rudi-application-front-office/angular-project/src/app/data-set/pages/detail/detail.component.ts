import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Licence, LicenceStandard, Media, MediaFile, MediaType, Metadata} from '../../../api-kaccess';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import * as mediaType from '../../../api-kaccess/model/media';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MatMenuTrigger} from '@angular/material/menu';
import saveAs from 'file-saver';
import {HttpResponse} from '@angular/common/http';
import {BehaviorSubject, combineLatest, from, Observable, of} from 'rxjs';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {KosMetierService} from '../../../core/services/kos-metier.service';
import {LanguageService} from '../../../i18n/language.service';
import {DetailFunctions} from './detail-functions';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ObservableUtils} from '../../../shared/utils/ObservableUtils';
import {Project} from '../../../projekt/projekt-model';
import {ProjektMetierService} from '../../../core/services/asset/project/projekt-metier.service';
import {PageResultUtils} from '../../../shared/utils/page-result-utils';
import {catchError, defaultIfEmpty, filter, map, mapTo, switchMap, take, tap} from 'rxjs/operators';
import {AuthenticationState} from '../../../core/services/authentication/authentication-method';
import {AuthenticationService} from '../../../core/services/authentication.service';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';
import {MatDialog} from '@angular/material/dialog';
import {ProjectSubmissionService} from '../../../core/services/asset/project/project-submission.service';
import {PageTitleService} from '../../../core/services/page-title.service';
import {CloseEvent, DialogClosedData} from '../../models/dialog-closed-data';
import {RequestDetails} from '../../../shared/models/request-details';
import {LinkedDatasetFromProject} from '../../models/linked-dataset-from-project';
import {DefaultMatDialogConfig} from '../../../core/services/default-mat-dialog-config';
import {
    SuccessRestrictedRequestDialogComponent
} from '../../components/success-restricted-request-dialog/success-restricted-request-dialog.component';
import {SimpleSkosConcept} from '../../../kos/kos-api';
import * as moment from 'moment';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {MetadataUtils} from '../../../shared/utils/metadata-utils';
import MediaTypeEnum = Media.MediaTypeEnum;
import LicenceTypeEnum = Licence.LicenceTypeEnum;
import {DisplayTableService} from '../../../core/services/data-set/display-table.service';
import {FiltersService} from '../../../core/services/filters.service';

const actionOnStartCreateLinkedDataset = 'ON_START_CREATE_LINKED_DATASET';

@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html',
    styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
    MAX_DATASETS_DISPLAYED = 3;
    @ViewChild('clickMenuFormatTrigger') clickMenuFormatTrigger: MatMenuTrigger;
    form: FormGroup;
    public selection: string;
    mediaType = mediaType.Media.MediaTypeEnum;
    mediaSize: MediaSize;
    mediaDataType = MediaTypeEnum;
    restrictedAccess: boolean;
    licenceLabel;
    conceptUri;
    themeLabel: string;
    _themePicto: string;
    downloadableMedias: Media[] = [];
    linkedProjects: Project[] = [];
    // Indique si on affiche le loader pendant le téléchargement du media
    public isLoading = false;
    otherDatasets: Metadata[] = [];
    // Pour filtrer à partir du même thème
    themeCode: string;
    totalOtherDatasets: number;
    mediasTitle: string;

    mediaToDisplayTable: Media;

    /**
     * Permet de suivre la valeur du JDD récupéré. Nous devons passer par un Observable car le chargement
     * asynchrone du JDD n'est pas implémenté de telle sorte que le composant se charge avec le JDD déjà chargé
     * par conséquent tous les traitements ayant besoin de l'objet JDD doivent commencer leur traitement uniquement
     * quand cet observable émet une valeur de JDD non nulle
     * @private
     */
    private metadataLoaded: BehaviorSubject<Metadata> = new BehaviorSubject<Metadata>(null);

    constructor(
        iconRegistryService: IconRegistryService,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly fb: FormBuilder,
        public dialog: MatDialog,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly kosMetierService: KosMetierService,
        private readonly route: ActivatedRoute,
        private readonly languageService: LanguageService,
        private readonly dataSetDetailsFunctions: DetailFunctions,
        private translateService: TranslateService,
        private snackBarService: SnackBarService,
        private projectSubmissionService: ProjectSubmissionService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly authenticationService: AuthenticationService,
        private readonly router: Router,
        private readonly activatedRoute: ActivatedRoute,
        private readonly propertiesMetierService: PropertiesMetierService,
        private readonly pageTitleService: PageTitleService
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.form = this.fb.group({
            options: []
        });
        iconRegistryService.addAllSvgIcons(ALL_TYPES);

    }

    private _metadata: Metadata | undefined;
    restrictedDatasetIcon = 'key_icon_88_secondary-color';
    selfDataIcon = 'self-data-icon';

    get metadata(): Metadata | undefined {
        return this._metadata;
    }

    set metadata(metadata: Metadata | undefined) {
        this._metadata = metadata;
        if (metadata) {
            this.metadataLoaded.next(metadata);
            this.loadLinkedProjects(metadata);
            this.loadOtherDatasets(metadata);
            this.pageTitleService.setPageTitle(metadata.resource_title, 'Détail');
            this.themeCode = this._metadata.theme;
        }
    }

    get selectedItem(): Media {
        return this.form.controls.options.value;
    }

    set selectedItem(selectedItem: Media) {
        this.form.setValue({
            options: selectedItem || null
        });
    }

    get isRestricted(): boolean {
        return MetadataUtils.isRestricted(this.metadata);
    }

    get isSelfdata(): boolean {
        return MetadataUtils.isSelfdata(this.metadata);
    }


    get isSpreadsheetDisplayed(): boolean {
        for (const item of this.metadata.available_formats) {
            const objet: MediaFile = item as MediaFile;
            if (objet.file_type === MediaType.TextCsv ||
                objet.file_type === MediaType.ApplicationVndMsExcel) {
                this.mediaToDisplayTable = item;
                return true;
            }
        }

        return false;
    }


    ngOnInit(): void {

        // Flow d'initialisation de la page
        this.route.params.pipe(
            tap(() => {
                this.isLoading = true;
                this.metadata = null;
            }),

            // On fait un appel REST pour récupèrer le JDD lié à partir de l'UUID dans la route
            switchMap((params: Params) => this.konsultMetierService.getMetadataByUuid(params.uuid)),

            /// On veut initialiser d'autres trucs une fois qu'on a récupéré le JDD
            tap((metadata: Metadata) => {
                if (metadata) {
                    this.metadata = metadata;
                    this.restrictedAccess = this.metadata?.access_condition?.confidentiality?.restricted_access;

                    // L'item sélectionné est le premier type FILE de la liste des formats disponibles
                    this.selectedItem = this.metadata.available_formats.filter(f => f.media_type === 'FILE')[0];
                    this.conceptUri = this.getConceptUri();
                    this.licenceLabel = this.getLicenceLabel();
                } else {
                    throw Error('Le JDD récupéré depuis le serveur est NUL, anormal, arrêt du traitement');
                }
            }),

            // on veut initialiser les médias téléchargeables
            switchMap((metadata: Metadata) => {
                return this.initDownloadableMedias().pipe(mapTo(metadata));
            }),

            // On veut initialiser les thèmes skos de la page
            switchMap((metadata: Metadata) => {
                const themeCode = metadata.theme;
                if (themeCode) {
                    this.isLoading = true;
                    return this.kosMetierService.getTheme(themeCode).pipe(
                        defaultIfEmpty(null),
                        map((themeConcept: SimpleSkosConcept) => {
                            // Si skos connaît le thème du JDD on remplit normalement
                            if (themeConcept) {
                                this.themeLabel = themeConcept.text;
                                this.themePicto = KosMetierService.getAssetNameFromConceptIcon(themeConcept.concept_icon);
                            }
                            // Si skos connaît pas le thème, on l'affiche brut de fonderie
                            else {
                                this.themeLabel = `[${themeCode}]`;
                            }
                            return metadata;
                        })
                    );
                }
                return of(metadata);
            })
        ).subscribe({
            next: () => {
                this.isLoading = false;
                this.mediasTitle = this.buildTitleDatasetCard(); // Quand on a les dépendances, on construit le titre
            },
            complete: () => {
                this.isLoading = false;
            },
            error: (e) => {
                this.isLoading = false;
                console.error(e);
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('error.technicalError'),
                    level: Level.ERROR
                });
            }
        });

        // On définit un comportement réactif :
        // Si la route contient l'action d'ouverture de popin et quand le JDD du détail est chargé
        combineLatest([this.route.queryParams, this.metadataLoaded]).pipe(
            // Alors on fait un traitement spécifique
            switchMap(([queryParams, metadata]) => {

                // Les 2 évènements ont bien eu lieu et sont définis
                const action = queryParams.action;
                if (action === actionOnStartCreateLinkedDataset && metadata != null) {

                    // Ouverture du workflow : demande d'accès
                    return this.openDialogsToCreateLinkedDataset();
                } else {

                    // interruption de l'évènement
                    return of(null);
                }
            }),

            // On continue si le workflow des popin a eu lieu
            filter((value) => {
                return value != null;
            })
        ).subscribe({
            // Sinon message d'erreur
            error: (e) => {
                console.error(e);
                this.snackBarService.add(
                    this.translateService.instant('metaData.error-request-access')
                );
            }
        });
    }

    get uuid(): string {
        return this.metadata?.global_id;
    }

    /**
     * Fonction permettant de verifier si available_formats n'est pas vide et affiche le media_type
     * @param mediaData
     */
    isAvailableFormat(mediaData: string): boolean {
        const result = this.metadata.available_formats.filter(element =>
            element.media_type === mediaData
        );
        return result.length > 0;
    }

    /**
     * Fonction permettant de construire la chaine de caractère correspondant au titre selon les média_type
     */
    buildTitleDatasetCard(): string {
        if (this.metadata.available_formats.length === 0) {
            return null;
        }
        let arrayTemporary: string[] = [];
        if (this.isAvailableFormat(this.mediaDataType.File)) {
            arrayTemporary.push(this.translateService.instant('metaData.file'));
        }
        if (this.isAvailableFormat(this.mediaDataType.Series)) {
            arrayTemporary.push(this.translateService.instant('metaData.series'));
        }
        if (this.isAvailableFormat(this.mediaDataType.Service)) {
            arrayTemporary.push(this.translateService.instant('metaData.service'));
        }
        if (arrayTemporary.length === 1) { // 1 seul element ? rien à faire comme formatage
            return arrayTemporary[0];
        }
        let lastElement = arrayTemporary.pop();
        return arrayTemporary.join(', ') + ' ' + this.translateService.instant('common.et') + ' ' + lastElement;
    }

    /**
     * Fonction permettant de retourner l'extension du fichier
     */
    getMediaFileExtension(media: Media): string {
        return this.konsultMetierService.getMediaFileExtension(media);
    }

    /**
     * Function permettant de récupérer le nom du fichier à télécharger
     * @param response
     */
    downLoadFile(response: HttpResponse<Blob>): void {
        const blob = new Blob([response.body], {type: response.body.type});
        const filename = response.headers.get('content-disposition').split(';')[1].split('filename')[1].split('=')[1].trim();
        saveAs(blob, filename);
    }

    /**
     * Fonction permettant de télécharger un fichier suivant son format
     */
    onDownloadFormat(): void {
        this.isLoading = true;
        const selectedItem = this.selectedItem;
        if (selectedItem) {

            this.konsultMetierService.downloadMetadataMedia(this.metadata.global_id, selectedItem.media_id)
                .subscribe(
                    (response) => {
                        this.isLoading = false;
                        this.downLoadFile(response);
                    },
                    () => {
                        this.isLoading = false;
                        const message = this.translateService.instant('common.echec');
                        const linkLabel = this.translateService.instant('snackbarTemplate.ici');
                        this.propertiesMetierService.get('rudidatarennes.contact').subscribe(link => {
                            this.snackBarService.openSnackBar({
                                message: `${message} <a href="${link}">${linkLabel}</a>.`,
                                level: Level.ERROR
                            });
                        });
                    });
            this.clickMenuFormatTrigger.closeMenu();
        }
    }

    getLicenceLabel(): Observable<string> {
        if (this.metadata.access_condition?.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getLicenceLabelFromCode(licenceCode);
        }
        return null;
    }

    getConceptUri(): Observable<string> {
        if (this.metadata.access_condition.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getConceptUriFromCode(licenceCode);
        }
        return null;
    }

    private loadLinkedProjects(metadata: Metadata): void {
        this.linkedProjects = [];
        PageResultUtils
            .fetchAllElementsUsing(offset =>
                this.projektMetierService.searchProjects({
                    dataset_uuids: [metadata.global_id],
                    offset
                }))
            .subscribe(projects => this.linkedProjects = projects as Project[]);
    }

    /**
     * Fonction permettant de charger les média téléchargeables
     * @private
     */
    private initDownloadableMedias(): Observable<void> {
        return ObservableUtils
            .filter(this.metadata.available_formats)
            .using(media => this.dataSetDetailsFunctions.canDownloadMedia(media, this.metadata))
            .pipe(
                map((downloadableMedias: Media[]) => {
                    this.downloadableMedias = downloadableMedias;
                })
            );
    }


    get themePicto(): string {
        return this._themePicto;
    }

    /** Example 'weather.svg' */
    @Input() set themePicto(themePicto: string) {
        this.matIconRegistry.addSvgIcon(
            themePicto,
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/pictos/' + themePicto)
        );
        this._themePicto = themePicto;
    }

    clickDeclareReuse(): Observable<boolean> {
        return from(this.router.navigate(['/projets/declarer-une-reutilisation'], {
            queryParams: {linkedDataset: this.metadata.global_id}
        }));
    }

    private goToLoginPage(queryParams: Params): Observable<boolean> {
        const promise = this.router.navigate(['/login'], {
            queryParams
        });

        return from(promise);
    }

    private loadOtherDatasets(metadata: Metadata): void {
        this.otherDatasets = [];
        this.konsultMetierService.getMetadatasWithSameTheme(metadata.global_id, this.MAX_DATASETS_DISPLAYED)
            .subscribe(otherDatasets => {
                this.otherDatasets = otherDatasets;
            });
        this.konsultMetierService.getNumberOfDatasetsOnTheSameTheme(this._metadata.global_id)
            .subscribe(result => this.totalOtherDatasets = result);
    }

    /**
     * Action déclenchée quand on clique sur le bouton de demande d'accès au JDD
     */
    handleClickRequestAccess(): void {
        this.authenticationService.authenticationChanged$.pipe(take(1)).pipe(
            switchMap((state: AuthenticationState) => {
                if (state === AuthenticationState.USER) {
                    return this.openDialogsToCreateLinkedDataset();
                } else {
                    return this.goToLoginPage({
                        redirectTo: `/catalogue/detail/${this.uuid}?action=${actionOnStartCreateLinkedDataset}`,
                        snackBar: 'project.buttonPopover.genericUnauthorizedMessage',
                    });
                }
            })
        ).subscribe({
            error: (e) => {
                console.error(e);
                this.snackBarService.add(
                    this.translateService.instant('metaData.error-request-access')
                );
            }
        });
    }

    /**
     * Action déclenchée au clic du bouton demande d'information
     */
    handleClickSelfdataInformationRequest(): void {
        this.router.navigate(['selfdata-information-request-creation'], {relativeTo: this.activatedRoute});
    }

    /**
     * Récupères l'icône à afficher à côté du titre du JDD
     * Rien ne sera affiché si concerné
     */
    getDatasetTitleIcon(): string {
        if (this.isRestricted) {
            return this.restrictedDatasetIcon;
        } else if (this.isSelfdata) {
            return this.selfDataIcon;
        }

        return null;
    }

    /**
     * Ouvre une popin d'info que l'enregistrement PROJET à bien marché
     */
    public openDialogSuccessLinkedDataset(): void {
        const dialogConfig = new DefaultMatDialogConfig();
        this.dialog.open(SuccessRestrictedRequestDialogComponent, dialogConfig);
    }

    /**
     * Workflow d'enchaînement de popin pour pouvoir faire une demande d'accès à ce JDD restreint
     * @private
     */
    private openDialogsToCreateLinkedDataset(): Observable<boolean> {

        // Le projet choisi dans l'enchaînement des popins
        let projectSelected: Project;

        // Les détails de la requêtes saisis dans l'enchaînement des popins
        let requestDetail: RequestDetails;

        // Cet observable est direct et ne vient pas d'une action d'aller-retour entre popins
        let isNotPreviousObservable = true;

        // C'est parti : ouverture popin séléction de projet
        return this.projectSubmissionService.selectProjectsDialog(this._metadata).pipe(
            // Récupération retour popin projet
            switchMap((closedDataStep1: DialogClosedData<Project>) => {

                // Si il a bien choisi le projet on continue vers la saisie des détails
                if (closedDataStep1.closeEvent === CloseEvent.VALIDATION) {
                    projectSelected = closedDataStep1.data;

                    // Si le projet a une date de fin on la récupère pour pré-remplir la date de fin d'accès
                    let endDate;
                    if (projectSelected.expected_completion_end_date) {
                        endDate = moment(projectSelected.expected_completion_end_date);
                    }

                    // On continue en ouvrant la popin de saisie des détails de la demande
                    return this.projectSubmissionService.openDialogRequestDetails(endDate);
                }
                // Sinon On arrête tout maintenant : annulation
                else {
                    return of(null);
                }
            }),

            // On Continue la chaîne d'observable que quand l'utilisateur veut saisir une requête,
            // CAD la valeur passée dans la chaîne n'est pas nulle
            filter((wantsToContinue) => (wantsToContinue)),

            // Récupération retour popin : détails
            switchMap((closedDataStep2: DialogClosedData<RequestDetails>) => {

                // Si l'utilisateur a décidé d'aller en arrière
                if (closedDataStep2.closeEvent === CloseEvent.PREVIOUS) {

                    // Cet observable n'est plus direct on ne va pas traiter la chaîne de suite on s'interesse
                    // que a l'observable recursif créé après
                    isNotPreviousObservable = false;

                    // On recommence tout debuit le début (ouverture popin projet etc.)
                    return this.openDialogsToCreateLinkedDataset();
                }
                // Si il annule carrément on stop toute la chaîne
                else if (closedDataStep2.closeEvent === CloseEvent.CANCEL) {
                    return of(null);
                }
                // Cas classique l'utilisateur a choisi les 2 données qu'on veut
                else {
                    requestDetail = closedDataStep2.data;
                    return of({
                        datasetUuid: this.metadata.global_id,
                        project: projectSelected,
                        requestDetail
                    });
                }
            }),

            // On Continue la chaîne d'observable que dans un cas valide (pas d'annulation aller-retour on traite que l'observable
            // qui concernce la saisie finale des données requises)
            filter((value) => (isNotPreviousObservable && value)),

            // On a choisi les données on va créer ce qu'il faut dans le back end
            switchMap((linkToCreate: LinkedDatasetFromProject) => this.callbackCreateLinkedDataset(linkToCreate)),

            // Renvoie vrai
            mapTo(true)
        );
    }

    /**
     * La callback appelée quand on veut vraiment créer la demande d'accès (JDD lié)
     * quand l'utilisateur finit de saisir le commentaire
     * @param linkToCreate l'objet saisi par l'enchaînement des popins
     * @private
     */
    private callbackCreateLinkedDataset(linkToCreate: LinkedDatasetFromProject): Observable<void> {
        if (linkToCreate) {
            this.isLoading = true;
            return this.projectSubmissionService.createLinkedDatasetFromProject(linkToCreate).pipe(
                catchError((error) => {
                    this.isLoading = false;
                    throw error;
                }),
                tap(() => {
                    this.isLoading = false;
                    this.openDialogSuccessLinkedDataset();
                })
            );
        }

        return of(null);
    }
}


