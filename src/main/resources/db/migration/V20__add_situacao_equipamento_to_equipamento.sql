/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

ALTER TABLE equipamento
    ADD COLUMN id_situacao_equipamento BIGINT NOT NULL;

ALTER TABLE equipamento
    ADD CONSTRAINT fk_equipamento_situacao
        FOREIGN KEY (id_situacao_equipamento)
        REFERENCES situacao_equipamento(id_situacao_equipamento);