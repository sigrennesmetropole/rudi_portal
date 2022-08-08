-- Renommage du user
UPDATE acl_data.user
SET firstname = 'strukture',
    lastname  = 'strukture',
    login     = 'strukture'
WHERE login = 'providers';

-- Renommage des rôles
UPDATE acl_data.role
SET code='MODULE_STRUKTURE',
    label='Module Strukture'
WHERE code = 'MODULE_PROVIDER';
UPDATE acl_data.role
SET code='MODULE_STRUKTURE_ADMINISTRATOR',
    label='Module administrateur Strukture'
WHERE code = 'MODULE_PROVIDER_ADMINISTRATOR';
