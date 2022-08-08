-- Report pour la V0.3 du script V4__RUDI-1936_Incoherence_creation_visualisation.sql créé en V0.2
-- Cf V2__RUDI-1936_Incoherence_creation_visualisation.sql dans Ansible

-- On généralise la modification pour n'importe quel autre type de projet ayant des codes dupliqués
-- Pour simplifier la compatibilité avec le H2 en mode PostgreSQL des tests, on remplace tous les types plutôt que de
-- chercher ceux dupliqués avec un WITH et un count(1) OVER (PARTITION BY code) AS count (cf historique Git de ce fichier).
UPDATE project_type
SET code = 'code-' || right(uuid::varchar, 25)
WHERE lower(label) != code;

-- Pour être compatible avec la V0.3 et rétrocompatible avec la V0.2 on ne supprime la contrainte que si elle existe
ALTER TABLE project_type
    DROP CONSTRAINT IF EXISTS UK_e3u3snmur8qbwbiez91va0dfp;
-- On recrée la contrainte
-- Modification de la table type, rendre la colonne code UNIQUE car cela est utilisé comme "value" des options du select
ALTER table if exists projekt_data.project_type
    add constraint UK_e3u3snmur8qbwbiez91va0dfp unique (code);
