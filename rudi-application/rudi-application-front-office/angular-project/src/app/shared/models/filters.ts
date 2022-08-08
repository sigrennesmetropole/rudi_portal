import {OrderValue} from '../../core/services/filters/order-filter';

export interface Filters {
  search: string;
  themes: string[];
  keywords: string[];
  producerNames: string[];
  dates: {
    debut: string;
    fin: string;
  };
  order: OrderValue;
  restrictedAccess: boolean;

  /** Global ID du ou des jeux de données */
  globalIds: string[];
}
