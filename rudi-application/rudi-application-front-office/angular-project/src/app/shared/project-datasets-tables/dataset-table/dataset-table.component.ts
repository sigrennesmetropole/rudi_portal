import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {RowTableData} from '@shared/project-datasets-tables/dataset.interface';

@Component({
    selector: 'app-dataset-table',
    templateUrl: './dataset-table.component.html',
    styleUrls: ['./dataset-table.component.scss']
})
export class DatasetTableComponent {
    displayedColumns: string[] = ['addedDate', 'title', 'status', 'comment-action', 'delete-action'];

    /**
     * titre du tableau
     */
    @Input()
    title: string;

    /**
     * Label RGAA pour le tableau
     */
    @Input()
    ariaLabel: string;

    /**
     * Données (lignes) à afficher dans le tableau
     */
    @Input()
    dataSource: MatTableDataSource<RowTableData>;

    /**
     * Boolean indiquant l'état du tableau (chargement des données fini ou pas)
     */
    @Input()
    tableLoading: boolean;

    /**
     * Boolen permettant de désactiver le bouton d'ajout (si un autre ajout est déjà en cours dans un tableau)
     */
    @Input()
    disableAddButton: boolean;

    /**
     * Détermine si on affiche le bouton d'ajout de dataset
     */
    @Input()
    hasAddButton: boolean;

    /**
     * Détermine si on affiche le bouton de suppression de dataset
     */
    @Input()
    hasDeleteButton: boolean;

    @Input()
    hasCommentButton: boolean;

    @Output()
    addActionEvent: EventEmitter<void>;

    @Output()
    deleteActionEvent: EventEmitter<RowTableData>;

    @Output()
    commentActionEvent: EventEmitter<RowTableData>;

    constructor() {
        this.dataSource = new MatTableDataSource([]);
        this.tableLoading = true;
        this.disableAddButton = false;
        this.hasAddButton = false;
        this.hasCommentButton = false;

        this.addActionEvent = new EventEmitter();
        this.deleteActionEvent = new EventEmitter();
        this.commentActionEvent = new EventEmitter();
    }

    addAction(): void {
        this.addActionEvent.emit();
    }

    deleteAction(element: RowTableData): void {
        this.deleteActionEvent.emit(element);
    }

    commentAction(element: RowTableData): void {
        this.commentActionEvent.emit(element);
    }
}
