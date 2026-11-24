ALTER TABLE users ADD COLUMN avatar_url TEXT;
ALTER TABLE users ADD COLUMN cover_url TEXT;

INSERT INTO permissions (id, name, code, description)
VALUES (gen_random_uuid(), 'Atualizar fotos do perfil', 'PROFILE_UPDATE', 'Permite atualizar o avatar e a capa do próprio perfil.');

INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.code IN ('admin', 'user') AND p.code = 'PROFILE_UPDATE';
