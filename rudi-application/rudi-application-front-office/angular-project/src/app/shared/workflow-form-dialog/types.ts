import {Form} from 'micro_service_modules/projekt/projekt-api';

export interface WorkflowFormDialogInputData {
    title: string;
    form: Form;
}

export interface WorkflowFormDialogOutputData {
    form: Form;
}
