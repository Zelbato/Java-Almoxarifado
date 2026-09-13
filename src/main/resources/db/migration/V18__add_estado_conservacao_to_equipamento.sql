/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

ALTER TABLE equipamento
    ADD COLUMN id_estado_conservacao BIGINT NOT NULL;

ALTER TABLE equipamento
    ADD CONSTRAINT fk_equipamento_estado_conservacao
        FOREIGN KEY (id_estado_conservacao)
        REFERENCES estado_conservacao(id_estado_conservacao);