import {AccessStatusFiltersType} from '@core/services/filters/access-status-filters-type';

export interface AddDataSetDialogData {
    accessStatusForcedValue: AccessStatusFiltersType;
    accessStatusHiddenValues?: AccessStatusFiltersType[];
}
