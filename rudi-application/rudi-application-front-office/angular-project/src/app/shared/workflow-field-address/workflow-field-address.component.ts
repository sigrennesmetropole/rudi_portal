import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, ValidationErrors, ValidatorFn, Validators} from '@angular/forms';
import {RvaService} from '@core/services/rva/rva.service';
import {SelfdataRvaService} from '@core/services/rva/selfdata/selfdata-rva.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {debounceTime, filter, map, switchMap} from 'rxjs/operators';
import {mapEach} from '../utils/ObservableUtils';
import {WorkflowFieldComponent} from '../workflow-field/workflow-field.component';
import {RvaAddress} from './workflow-field-address.model';

@Component({
    selector: 'app-workflow-field-address',
    templateUrl: './workflow-field-address.component.html',
    styleUrls: ['./workflow-field-address.component.scss']
})
export class WorkflowFieldAddressComponent extends WorkflowFieldComponent implements OnInit {
    private selectedAddress: RvaAddress = null;
    addressList$: Observable<RvaAddress[]>;
    userInputFormControl: AbstractControl;
    adressLoading: boolean;
    hasError = false;

    constructor(
        private readonly addressMetierService: SelfdataRvaService,
        private readonly formBuilder: FormBuilder,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
    ) {
        super();
    }

    get readonly(): boolean {
        return super.readonly;
    }

    addOtherControls(): void {
        if (!this.readonly) {
            const validators = [this.checkRvaAddressValidator.bind(this)];
            if (this.required) {
                validators.push(Validators.required);
            }
            this.userInputFormControl = this.formBuilder.control('', validators);
            this.formGroup.addControl(this.userInputFormControlName, this.userInputFormControl);
        }
    }

    get userInputFormControlName(): string {
        return this.formControlName + '-user-input';
    }

    get falseValidator(): ValidatorFn {
        return Validators.minLength(Number.MAX_VALUE);
    }

    ngOnInit(): void {
        if (!this.readonly) {
            this.setAddressAutocomplete();
        } else {
            this.setAddressValueInField();
        }
    }

    private setAddressValueInField(): void {
        this.userInputFormControl = this.formBuilder.control('', [this.falseValidator]);
        this.formGroup.addControl(this.userInputFormControlName, this.userInputFormControl);
        this.setAddressInReadOnlyForm(this.formControl.value);
    }

    private setAddressAutocomplete(): void {
        this.addressList$ = this.userInputFormControl.valueChanges.pipe(
            filter(inputValue => typeof inputValue === 'string'),
            map(userInput => userInput as string),
            debounceTime(200),
            switchMap(userInput => {
                this.formControl.reset();
                return userInput ? this.getAddressesFromApi(userInput) : of([]);
            })
        );
        this.formControl.valueChanges.subscribe(() => this.updateValidity(this.userInputFormControl));
    }

    formatAddress(address: RvaAddress): string {
        return address?.label ?? '';
    }

    private getAddressesFromApi(query: string): Observable<RvaAddress[]> {
        if (!RvaService.isValidQuery(query)) {
            return of([]); // Renvoie le tableau comme un élément dans un subscribe
        }
        return this.addressMetierService.getFullAddresses(this.processQuery(query))
            .pipe(
                mapEach(anAddress => ({id: anAddress.idaddress, label: anAddress.addr3, addr2: anAddress.addr2} as RvaAddress))
            );
    }

    /**
     * Vérifier si le champ de saisie du user contient une addr2, si oui faire la recherche sur ça
     * car => on supprime le contenu du champ en commençant par la fin (qui vaut la ville dans une adresse)
     * Utile quand on sélectionne une adresse et décide de revenir dessus
     * @param query contenu du champ de saisie de l'adresse
     * @private
     */
    private processQuery(query: string): string {
        if (this.selectedAddress !== null) {
            // Si addr2 est inclus dans notre query
            return (query.search(this.selectedAddress.addr2) !== -1) ? this.selectedAddress.addr2 : query;
        }
        return query;
    }

    setSelectedAddress(selectedAddress: RvaAddress | undefined): void {
        this.selectedAddress = selectedAddress;
        this.formControl.setValue(this.selectedAddress?.id);
    }

    /**
     * On met à jour la validité du champ de saisie des adresses,
     * car elle est vérifiée avant la prise en compte de la sélection d'une adresse.
     */
    private updateValidity(formControl: AbstractControl): void {
        formControl.updateValueAndValidity({
            emitEvent: false
        });
    }

    /**
     * Ne pas autoriser une adresse non RVA.
     * Attention ce validateur est exécuté dès la modification de l'input, avant même que le selectedAddress soit positionné
     * @param userInputFormControl saisie utilisateur pour filtrer les adresses
     */
    checkRvaAddressValidator(userInputFormControl: AbstractControl): ValidationErrors | null {
        return !userInputFormControl.value || this.formControl.value ? null : {checkRvaAddressValidator: true};
    }

    getAddressById(addressId: number) {
        return this.addressMetierService.getAddressById(addressId)
            .pipe(map(anAddress => ({id: anAddress.idaddress, label: anAddress.addr3, addr2: anAddress.addr2} as RvaAddress))
            );
    }

    setAddressInReadOnlyForm(addressId: number) {
        this.hasError = false;
        this.adressLoading = true;
        this.getAddressById(addressId).subscribe({
            next: (result: RvaAddress) => {
                this.formGroup.get(this.userInputFormControlName).clearValidators();
                this.formGroup.get(this.userInputFormControlName).setValue(this.formatAddress(result));
                this.hasError = false;
            },
            complete: () => {
                this.adressLoading = false;
            },
            error: (e) => {
                this.adressLoading = false;
                console.error(e);
                this.hasError = true;
                this.formGroup.get(this.userInputFormControlName).markAsTouched();
                this.formGroup.get(this.userInputFormControlName).setValue(
                    this.translateService.instant('metaData.selfdataInformationRequest.consultation.addressError')
                );
            }
        });
    }
}
