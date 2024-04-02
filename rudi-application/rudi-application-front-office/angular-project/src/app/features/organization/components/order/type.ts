export type Order =
    'name' |
    '-name' |
    'openingDate' |
    '-openingDate';

export interface OrderItem {
    libelle: string;
    order: Order;
}
