INSERT IGNORE INTO usuario (
    fk_funcionario,
    id_tipo_usuario,
    nome,
    email,
    senha,
    cpf,
    data_nascimento,
    ativo
)
SELECT
    NULL,
    id_tipo_usuario,
    'Almoxarifado Chefe',
    'chefe@almoxarifado.local',
    '$2a$12$NsaXDrmkXjvMECYvcdtIdOHxgXziPea0bBdg.pAwxZajEFdCkA4d6',
    '00000000000',
    NULL,
    TRUE
FROM tipo_usuario
WHERE nome = 'ALMOXARIFADO_CHEFE'
LIMIT 1;

INSERT IGNORE INTO usuario (
    fk_funcionario,
    id_tipo_usuario,
    nome,
    email,
    senha,
    cpf,
    data_nascimento,
    ativo
)
SELECT
    NULL,
    id_tipo_usuario,
    'Almoxarifado Junior',
    'junior@almoxarifado.local',
    '$2a$12$vGjf0bpDT9V5fFw/FI5k9eWKzUsqmTqMHGZxdZ4mq5XGhQdED9b5a',
    '00000000001',
    NULL,
    TRUE
FROM tipo_usuario
WHERE nome = 'ALMOXARIFADO_JUNIOR'
LIMIT 1;
