import {Component, Input, OnInit} from '@angular/core';
import {Sort} from '@angular/material/sort';
import {CloseEvent, DialogClosedData} from '@features/data-set/models/dialog-closed-data';
import {MediaSize} from '@core/services/breakpoint-observer.service';
import {LogService} from '@core/services/log.service';
import {DialogMemberOrganizationService} from '@core/services/organization/dialog-member-organization.service';
import {OrganizationMetierService} from '@core/services/organization/organization-metier.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {BackPaginationSort} from '@shared/back-pagination/back-pagination-sort';
import {SortTableInterface} from '@shared/back-pagination/sort-table-interface';
import {Level} from '@shared/notification-template/notification-template.component';
import {OrganizationRole, OrganizationUserMember} from 'micro_service_modules/strukture/api-strukture';
import {Organization, OrganizationMember, PagedOrganizationUserMembers} from 'micro_service_modules/strukture/strukture-model';
import {BehaviorSubject, combineLatest, EMPTY, merge, Observable, of} from 'rxjs';
import {debounceTime, filter, map, mapTo, switchMap, tap} from 'rxjs/operators';
import {OrganizationMemberDialogData} from './organization-member-dialog-data';

@Component({
    selector: 'app-organization-members-table',
    templateUrl: './organization-members-table.component.html',
    styleUrls: ['./organization-members-table.component.scss']
})
export class OrganizationMembersTableComponent implements OnInit {

    @Input() organization: Organization;
    _organizationMembersList: OrganizationUserMember[] = [];
    mediaSize: MediaSize;
    isMenuOpen = false;
    hasError = false;
    DEFAULT_ITEMS_PER_PAGE = 10;
    DEFAULT_SORT_ORDER = '-added_date';
    displayedColumns: string[] = ['lastname', 'firstname', 'login', 'last_connexion', 'added_date', 'role', 'dash'];
    searchIsRunning = false;
    isSearchError = false;

    DEFAULT_SORT = {order: this.DEFAULT_SORT_ORDER, page: 1};
    backPaginationSort = new BackPaginationSort();
    currentSortTable: SortTableInterface;
    currentPage: number;
    membersTotal = 0;
    searchText = '';
    itemsPerPage = this.DEFAULT_ITEMS_PER_PAGE;

    userTypedEvent = new BehaviorSubject<string>(null);
    userTypedSearchInput: Observable<string> = this.userTypedEvent.pipe(
        filter((value) => value != null),
        debounceTime(500)
    );

    userArrivedOnPage: Observable<string> = of('');

    userResetEvent = new BehaviorSubject<boolean>(false);
    userClickedReset: Observable<string> = this.userResetEvent.pipe(
        filter((isTriggered) => isTriggered),
        map(() => ''),
        debounceTime(100)
    );

    userInputChanged: Observable<string> = merge(this.userArrivedOnPage, this.userTypedSearchInput, this.userClickedReset).pipe(
        tap((userInput: string) => {
            this.searchText = userInput;
            this.backPaginationSort.currentPage = 1;
        })
    );

    paginationIsReset: Observable<SortTableInterface> = this.userInputChanged.pipe(
        mapTo(this.DEFAULT_SORT)
    );

    launchSearchEvent = new BehaviorSubject<SortTableInterface>(this.DEFAULT_SORT);
    userPaginationChanged: Observable<SortTableInterface> = merge(this.launchSearchEvent, this.paginationIsReset).pipe(
        filter((value) => value != null),
    );

    searchOrganizationMembersEvent: Observable<PagedOrganizationUserMembers>
        = combineLatest([this.userInputChanged, this.userPaginationChanged]).pipe(
        switchMap((combination: [string, SortTableInterface]) => {
            const userInput: string = combination[0];
            const sort: SortTableInterface = combination[1];
            this.searchIsRunning = true;
            this.hasError = false;
            return this.searchOrganizationMembers(sort, userInput);
        })
    );

    constructor(private readonly organizationMetierService: OrganizationMetierService,
                private readonly translateService: TranslateService,
                private readonly logService: LogService,
                private readonly propertiesMetierService: PropertiesMetierService,
                private readonly snackbarService: SnackBarService,
                private readonly dialogMemberOrganizationService: DialogMemberOrganizationService,
    ) {
    }

    ngOnInit(): void {
        this.searchOrganizationMembersEvent.subscribe({
            next: (page: PagedOrganizationUserMembers) => this.handleSearchOrganizationMembersSuccess(page),
            error: (error) => this.handleSearchOrganizationMembersError(error)
        });
    }

    get organizationMembersList(): OrganizationUserMember[] {
        return this._organizationMembersList;
    }

    set organizationMembersList(list: OrganizationUserMember[]) {
        this._organizationMembersList = list;
    }

    onUserTyped(event): void {
        if (event != null && event.target != null) {
            this.userTypedEvent.next(event.target.value);
        }
    }

    onReset(): void {
        this.userResetEvent.next(true);
    }

    onPagination(sortTableInterface: SortTableInterface): void {
        this.launchSearchEvent.next(sortTableInterface);
    }

    onUserSort(sort: Sort): void {
        if (!sort.active || sort.direction === '') {
            return;
        } else {
            this.backPaginationSort.currentPage = this.currentPage;
            this.launchSearchEvent.next(this.backPaginationSort.sortTable(sort));
        }
    }

    private searchOrganizationMembers(sortTableInterface: SortTableInterface, searchText: string)
        : Observable<PagedOrganizationUserMembers> {
        const page = sortTableInterface?.page;
        const order = sortTableInterface?.order;
        this.currentPage = page;
        return this.organizationMetierService.searchOrganizationMembers(this.organization.uuid, searchText, (page - 1) >= 0 ?
            (page - 1) * this.itemsPerPage : 0, this.itemsPerPage, order);
    }

    private handleSearchOrganizationMembersSuccess(organizationMembers: PagedOrganizationUserMembers): void {
        this.searchIsRunning = false;
        this.isSearchError = false;
        const wrapper = organizationMembers.elements;
        if (wrapper == null) {
            this._organizationMembersList = [];
        } else {
            this._organizationMembersList = wrapper;
        }
        this.membersTotal = organizationMembers.total;
    }

    private handleSearchOrganizationMembersError(error: Error): void {
        this.searchIsRunning = false;
        this.isSearchError = true;
        this.logService.error(error);
        this.snackbarService.openSnackBar({
            message: this.translateService.instant('metaData.administrationTab.membersTable.errorPagination'),
            level: Level.ERROR,
        }, 3000);
    }

    computeRoleLabe(role: OrganizationRole): string {
        if (role === OrganizationRole.Editor) {
            return this.translateService.instant('metaData.administrationTab.membersTable.editor') + ' ';
        } else if (role === OrganizationRole.Administrator) {
            return this.translateService.instant('metaData.administrationTab.membersTable.administrator') + ' ';
        } else {
            return '';
        }
    }

    handleDisplayNoResults(): string {
        if (this.searchText != null && this.searchText.length > 0) {
            return this.translateService.instant('metaData.administrationTab.membersTable.noResultWithFilter');
        }

        return this.translateService.instant('metaData.administrationTab.membersTable.noResult');
    }

    get total(): unknown {
        if (!this.searchIsRunning) {
            return this.membersTotal;
        } else {
            return '...';
        }
    }

    /**
     * Quand l'utilisateur click sur le lien equipe technique Rudi
     */
    handleClickContactRudi(): void {
        this.propertiesMetierService.get('rudidatarennes.contact').subscribe(link => {
            window.location.href = link;
        });
    }


    /**
     * Quand l'utilisateur click sur le bouton d'ajout de membre
     */
    handleClickAddMember(): void {
        this.searchIsRunning = true;
        const organizationMemberDialogData: OrganizationMemberDialogData = {};
        organizationMemberDialogData.organizationUuid = this.organization.uuid;
        organizationMemberDialogData.title = this.translateService.instant('metaData.administrationTab.membersTable.addPopin.title');
        organizationMemberDialogData.subTitle = this.translateService.instant('metaData.administrationTab.membersTable.addPopin.subTitle');
        organizationMemberDialogData.fieldLoginDescription = this.translateService.instant('metaData.administrationTab.membersTable.addPopin.descriptionMail');
        organizationMemberDialogData.fieldRoleDescription = this.translateService.instant('metaData.administrationTab.membersTable.addPopin.descriptionRole');
        const organizationMember$ = this.dialogMemberOrganizationService.openDialogAddMember(organizationMemberDialogData);
        this.addOrganizationMember(organizationMember$, this.organization.uuid).pipe(
            tap(() => this.userTypedEvent.next(this.searchText))
        ).subscribe({
                next: () => {
                    this.searchIsRunning = false;
                    this.snackbarService.openSnackBar({
                        message: this.translateService.instant('metaData.administrationTab.membersTable.addPopin.success'),
                        level: Level.INFO
                    });
                },
                complete: () => {
                    this.searchIsRunning = false;
                    this.hasError = false;
                },
                error: err => {
                    if (err.status === 401 || err.status === 404) {
                        this.snackbarService.openSnackBar({
                            message: err.error.label,
                            level: Level.ERROR
                        });
                    } else {
                        this.hasError = true;
                    }
                    this.searchIsRunning = false;
                }
            });
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Modifier le profil", ouverture de popin
     */
    public handleClickUpdateMember(organizationUserMember: OrganizationUserMember): void {
        this.searchIsRunning = true;
        const organizationMemberDialogData: OrganizationMemberDialogData = {};
        organizationMemberDialogData.organizationUserMember = organizationUserMember;
        organizationMemberDialogData.organizationUuid = this.organization.uuid;
        organizationMemberDialogData.title = this.translateService.instant('metaData.administrationTab.membersTable.updatePopin.title');
        organizationMemberDialogData.subTitle = this.translateService.instant('metaData.administrationTab.membersTable.updatePopin.text');
        organizationMemberDialogData.fieldLoginDescription = this.translateService.instant('metaData.administrationTab.membersTable.updatePopin.descriptionMail');
        organizationMemberDialogData.fieldRoleDescription = this.translateService.instant('metaData.administrationTab.membersTable.updatePopin.descriptionRole');
        const organizationMember$ = this.dialogMemberOrganizationService.openDialogUpdateMember(organizationMemberDialogData);
        this.updateOrganizationMember(organizationMember$, this.organization.uuid)
            .pipe(
                tap(() => this.userTypedEvent.next(this.searchText))
            )
            .subscribe({
                next: () => {
                    this.searchIsRunning = false;
                    this.snackbarService.openSnackBar({
                        message: this.translateService.instant('metaData.administrationTab.membersTable.updatePopin.success'),
                        level: Level.INFO
                    });
                },
                complete: () => {
                    this.searchIsRunning = false;
                    this.hasError = false;
                },
                error: err => {
                    // S'il y a eu une erreur
                    console.error(err);
                    if (err.status === 422) {
                        this.snackbarService.openSnackBar({
                            message: err.error.label,
                            level: Level.ERROR
                        });
                    } else {
                        this.hasError = true;
                    }
                    this.searchIsRunning = false;
                }
            });
    }

    /**
     * Action déclenchée lors du clic sur le bouton "Détacher de l'organisation", ouverture de popin
     * @param organizationUserMember
     */
    handleClickDeleteMember(organizationUserMember: OrganizationUserMember): void {
        this.searchIsRunning = true;
        const organizationMemberDialogData: OrganizationMemberDialogData = {};
        organizationMemberDialogData.organizationUserMember = organizationUserMember;
        organizationMemberDialogData.organizationUuid = this.organization.uuid;
        const organizationMember$ = this.dialogMemberOrganizationService.openDialogDeletionConfirmation(organizationMemberDialogData);
        this.removeOrganizationMember(organizationMember$, this.organization.uuid)
            .pipe(
                tap(() => this.userTypedEvent.next(this.searchText))
            )
            .subscribe({
                    next: () => {
                        this.searchIsRunning = false;
                        this.snackbarService.openSnackBar({
                            message: this.translateService.instant('metaData.administrationTab.membersTable.deletePopin.success'),
                            level: Level.INFO
                        });
                    },
                    complete: () => {
                        this.searchIsRunning = false;
                        this.hasError = false;
                    },
                    error: err => {
                        console.error(err);
                        if (err.status === 422) {
                            this.snackbarService.openSnackBar({
                                message: err.error.label,
                                level: Level.ERROR
                            });
                        } else {
                            this.hasError = true;
                        }
                        this.searchIsRunning = false;
                    }
                }
            );
    }

    /**
     * Fermeture de la popin d'ajout
     * @param organizationMember$ Observable retourné par le CloseEvent
     * @param organizationUuid uuid de l'organisation qu'on gère
     */
    addOrganizationMember(organizationMember$: Observable<DialogClosedData<OrganizationMember>>,
                          organizationUuid: string): Observable<OrganizationMember> {
        return organizationMember$.pipe(
            switchMap((organizationMember: DialogClosedData<OrganizationMember>) => {
                if (organizationMember == null || organizationMember.closeEvent == null ||
                    organizationMember.closeEvent !== CloseEvent.VALIDATION) {
                    return EMPTY;
                }
                return this.organizationMetierService.addOrganizationMember(organizationUuid, organizationMember.data);
            })
        );
    }

    updateOrganizationMember(organizationMember$: Observable<DialogClosedData<OrganizationMember>>,
                             organizationUuid: string): Observable<OrganizationMember> {
        return organizationMember$.pipe(
            switchMap((organizationMember: DialogClosedData<OrganizationMember>) => {
                if (organizationMember == null || organizationMember.closeEvent == null ||
                    organizationMember.closeEvent !== CloseEvent.VALIDATION) {
                    return EMPTY;
                }
                return this.organizationMetierService.updateOrganizationMember(organizationUuid,
                    organizationMember.data.user_uuid, organizationMember.data);
            })
        );
    }

    removeOrganizationMember(organizationMember$: Observable<DialogClosedData<OrganizationMember>>,
                             organizationUuid: string): Observable<OrganizationMember> {
        return organizationMember$.pipe(
            switchMap((organizationMember: DialogClosedData<OrganizationMember>) => {
                if (organizationMember == null || organizationMember.closeEvent == null ||
                    organizationMember.closeEvent !== CloseEvent.VALIDATION) {
                    return EMPTY;
                }
                return this.organizationMetierService.removeOrganizationMember(organizationUuid,
                    organizationMember.data.user_uuid);
            })
        );
    }
}
