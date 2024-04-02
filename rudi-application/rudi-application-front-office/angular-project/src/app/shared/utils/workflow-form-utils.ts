import {Form, Section} from 'micro_service_modules/api-bpmn';

/**
 * Récupère les sections du formulaire contenant au moins 1 champ
 * @param form le formulaire testé
 */
export function getSectionWithFields(form: Form): Section[] {
    if (form == null || form.sections == null || form.sections.length === 0) {
        return [];
    }
    return form.sections.filter((section: Section) => section.fields != null && section.fields.length > 0);
}
