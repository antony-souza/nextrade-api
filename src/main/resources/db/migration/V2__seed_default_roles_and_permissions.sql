INSERT INTO roles (id, name, code, level) VALUES
    (gen_random_uuid(), 'Administrador', 'admin', 999),
    (gen_random_uuid(), 'Usuário', 'user', 0);

INSERT INTO permissions (id, name, code, description) VALUES
    (gen_random_uuid(), 'Consultar perfil', 'PROFILE_READ', 'Permite consultar o próprio perfil.'),
    (gen_random_uuid(), 'Consultar usuários', 'USER_READ', 'Permite consultar os usuários cadastrados.'),
    (gen_random_uuid(), 'Alterar role de usuários', 'USER_UPDATE', 'Permite alterar a role de um usuário.'),
    (gen_random_uuid(), 'Consultar roles', 'ROLE_READ', 'Permite consultar as roles cadastradas.'),
    (gen_random_uuid(), 'Criar roles', 'ROLE_CREATE', 'Permite cadastrar roles.'),
    (gen_random_uuid(), 'Atualizar roles', 'ROLE_UPDATE', 'Permite editar roles.'),
    (gen_random_uuid(), 'Consultar permissões', 'PERMISSION_READ', 'Permite consultar o catálogo de permissões.'),
    (gen_random_uuid(), 'Criar permissões', 'PERMISSION_CREATE', 'Permite cadastrar permissões.'),
    (gen_random_uuid(), 'Consultar vínculos', 'ROLEPERMISSION_READ', 'Permite consultar permissões vinculadas às roles.'),
    (gen_random_uuid(), 'Conceder permissões', 'ROLEPERMISSION_CREATE', 'Permite vincular permissões a roles.'),
    (gen_random_uuid(), 'Revogar permissões', 'ROLEPERMISSION_DELETE', 'Permite remover vínculos de permissões das roles.');

INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r CROSS JOIN permissions p
WHERE r.code = 'admin' OR (r.code = 'user' AND p.code = 'PROFILE_READ');
