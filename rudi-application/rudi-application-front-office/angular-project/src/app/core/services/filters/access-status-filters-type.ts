export type AccessStatusFiltersType = 'OPENED' | 'RESTRICTED' | 'GDPR_SENSITIVE';
export const AccessStatusFiltersType = {
    Opened: 'true' as AccessStatusFiltersType,
    Restricted: 'false' as AccessStatusFiltersType,
    GdprSensitive: 'gdpr_sensitive' as AccessStatusFiltersType,
};
