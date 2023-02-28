export interface RvaAddress {
    id: number;
    /**
     * Champ affiché dans le mat-auto complete (addr3 dans l'API RVA)
     */
    label: string;
    /**
     * Champ sur lequel l'API RVA fait la recherche d'adresse
     */
    addr2: string;
}
