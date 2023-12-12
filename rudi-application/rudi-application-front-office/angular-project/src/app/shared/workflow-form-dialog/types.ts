import {Form} from '@app/projekt/projekt-api';

export interface WorkflowFormDialogInputData {
    title: string;
    form: Form;
}

export interface WorkflowFormDialogOutputData {
    form: Form;
}
