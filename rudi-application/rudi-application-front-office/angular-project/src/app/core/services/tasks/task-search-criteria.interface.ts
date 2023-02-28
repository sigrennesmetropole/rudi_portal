import {Status} from '../../../api-bpmn';

export interface TaskSearchCriteria {
    title?: string;
    description?: string;
    processDefinitionKeys?: string[];
    status?: Status[];
    fonctionalStatus?: string[];
    asAdmin?: boolean;
}
