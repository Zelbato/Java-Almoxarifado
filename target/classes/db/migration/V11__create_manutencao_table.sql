/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

CREATE TABLE manutencao (
    id_manutencao BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_equipamento BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    id_tipo_manutencao BIGINT NOT NULL,
    id_status_manutencao BIGINT NOT NULL,

    descricao_problema VARCHAR(500) NOT NULL,
    servico_realizado VARCHAR(500),
    tecnico_responsavel VARCHAR(150),

    data_entrada DATETIME NOT NULL,
    data_prevista_conclusao DATETIME,
    data_conclusao DATETIME,

    observacao VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_manutencao_equipamento
        FOREIGN KEY (id_equipamento)
        REFERENCES equipamento(id_equipamento),

    CONSTRAINT fk_manutencao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_manutencao_tipo
        FOREIGN KEY (id_tipo_manutencao)
        REFERENCES tipo_manutencao(id_tipo_manutencao),

    CONSTRAINT fk_manutencao_status
        FOREIGN KEY (id_status_manutencao)
        REFERENCES status_manutencao(id_status_manutencao)
);