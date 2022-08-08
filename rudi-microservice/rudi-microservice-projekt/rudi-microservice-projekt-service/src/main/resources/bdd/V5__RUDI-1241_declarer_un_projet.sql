-- Les projets précédemment créés étaient en fait des réutilisations
-- On doit donc les passer du statut EN_COURS au statut VALIDE (cf classe ProjectStatus)
UPDATE project
SET status = 'VALIDE'
WHERE status = 'EN_COURS';
