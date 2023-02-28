import {DictionaryEntry} from '../../../api-kaccess';

export interface GdataDataInterface {
    title?: string;
    description?:string;
    genericDataObject?:GenericDataObject;
}
export interface  GenericDataObject{
    legend?: Array<DictionaryEntry>;
    data?: Array<GenericData>;
}
export interface GenericData {
    label?: Array<DictionaryEntry>;
    value?: string;
}
