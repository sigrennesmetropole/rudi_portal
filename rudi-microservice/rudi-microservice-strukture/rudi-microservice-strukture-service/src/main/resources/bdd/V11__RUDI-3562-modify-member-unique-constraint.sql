-- Set organization member unique constraint to (organization_fk, user_uuid)
-- Delete old constraint
ALTER TABLE strukture_data.organization_member
    DROP CONSTRAINT organization_member_uk;

-- Delete double occurrence
-- Supprime les doublons qui sont EDITOR car l'ancienne contrainte ne permettait pas d'avoir des doublons sur le même role
DELETE FROM strukture_data.organization_member
WHERE (organization_fk, user_uuid, role)
          IN (
          SELECT organization_fk, user_uuid, role
          FROM strukture_data.organization_member
          WHERE role = 'EDITOR' and (organization_fk, user_uuid)
              IN (SELECT organization_fk, user_uuid
                  FROM strukture_data.organization_member
                  GROUP BY (organization_fk, user_uuid) HAVING count(user_uuid)>1)
      );

-- Add new constraint
ALTER TABLE strukture_data.organization_member
    ADD CONSTRAINT organization_member_uk UNIQUE (organization_fk, user_uuid);