/**
 * Author:  heito
 * O sistema passa a ter so os perfis ALMOXARIFADO_CHEFE e ALMOXARIFADO_JUNIOR.
 * Usuarios que estavam nos perfis antigos sao movidos antes de apagar.
 */

SET @id_chefe = (SELECT id_tipo_usuario FROM tipo_usuario WHERE nome = 'ALMOXARIFADO_CHEFE');
SET @id_junior = (SELECT id_tipo_usuario FROM tipo_usuario WHERE nome = 'ALMOXARIFADO_JUNIOR');

UPDATE usuario
SET id_tipo_usuario = @id_chefe
WHERE id_tipo_usuario IN (
    SELECT id_tipo_usuario FROM tipo_usuario
    WHERE nome IN ('ADMINISTRADOR', 'GERENTE')
);

UPDATE usuario
SET id_tipo_usuario = @id_junior
WHERE id_tipo_usuario IN (
    SELECT id_tipo_usuario FROM tipo_usuario
    WHERE nome IN ('ALMOXARIFE', 'FUNCIONARIO')
);

DELETE FROM tipo_usuario
WHERE nome IN ('ADMINISTRADOR', 'GERENTE', 'ALMOXARIFE', 'FUNCIONARIO');
