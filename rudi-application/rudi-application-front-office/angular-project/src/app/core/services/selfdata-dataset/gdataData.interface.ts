import {DictionaryEntry} from 'micro_service_modules/api-kaccess';

export interface GdataDataInterface {
    title?: string;
    description?: string;
    genericDataObject?: GenericDataObject;
}
export interface  GenericDataObject{
    legend?: DictionaryEntry[];
    data?: GenericData[];
}
export interface GenericData {
    label?: DictionaryEntry[];
    value?: string;
}
