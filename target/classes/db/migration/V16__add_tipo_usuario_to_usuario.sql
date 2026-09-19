/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

ALTER TABLE usuario
    ADD COLUMN id_tipo_usuario BIGINT NOT NULL;

ALTER TABLE usuario
    ADD CONSTRAINT fk_usuario_tipo_usuario
        FOREIGN KEY (id_tipo_usuario)
        REFERENCES tipo_usuario(id_tipo_usuario);