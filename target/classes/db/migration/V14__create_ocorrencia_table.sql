/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

CREATE TABLE ocorrencia (
    id_ocorrencia BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_equipamento BIGINT NOT NULL,
    id_movimentacao BIGINT NULL,
    id_funcionario BIGINT NULL,
    id_usuario_registro BIGINT NOT NULL,

    id_tipo_ocorrencia BIGINT NOT NULL,
    id_status_ocorrencia BIGINT NOT NULL,

    titulo VARCHAR(150) NOT NULL,
    descricao VARCHAR(500) NOT NULL,

    data_ocorrencia DATETIME NOT NULL,

    solucao VARCHAR(500),
    data_resolucao DATETIME NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ocorrencia_equipamento
        FOREIGN KEY (id_equipamento)
        REFERENCES equipamento(id_equipamento),

    CONSTRAINT fk_ocorrencia_movimentacao
        FOREIGN KEY (id_movimentacao)
        REFERENCES movimentacao(id_movimentacao),

    CONSTRAINT fk_ocorrencia_funcionario
        FOREIGN KEY (id_funcionario)
        REFERENCES funcionario(id_funcionario),

    CONSTRAINT fk_ocorrencia_usuario
        FOREIGN KEY (id_usuario_registro)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_ocorrencia_tipo
        FOREIGN KEY (id_tipo_ocorrencia)
        REFERENCES tipo_ocorrencia(id_tipo_ocorrencia),

    CONSTRAINT fk_ocorrencia_status
        FOREIGN KEY (id_status_ocorrencia)
        REFERENCES status_ocorrencia(id_status_ocorrencia)
);
