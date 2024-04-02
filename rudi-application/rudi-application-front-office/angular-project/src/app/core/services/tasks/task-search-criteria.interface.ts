import {Status} from 'micro_service_modules/api-bpmn';

export interface TaskSearchCriteria {
    title?: string;
    description?: string;
    processDefinitionKeys?: string[];
    status?: Status[];
    fonctionalStatus?: string[];
    asAdmin?: boolean;
}
