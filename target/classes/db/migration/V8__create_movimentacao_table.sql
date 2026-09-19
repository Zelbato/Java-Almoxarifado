/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

CREATE TABLE movimentacao (
    id_movimentacao BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_equipamento BIGINT NOT NULL,
    id_funcionario BIGINT NOT NULL,
    id_usuario_retirada BIGINT NOT NULL,
    id_usuario_devolucao BIGINT NULL,
    id_status_movimentacao BIGINT NOT NULL,

    data_retirada DATETIME NOT NULL,
    data_devolucao DATETIME NULL,
    data_prevista_devolucao DATETIME NOT NULL,

    finalidade VARCHAR(255),
    observacao_retirada VARCHAR(500),
    observacao_devolucao VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_movimentacao_equipamento
        FOREIGN KEY (id_equipamento)
        REFERENCES equipamento(id_equipamento),

    CONSTRAINT fk_movimentacao_funcionario
        FOREIGN KEY (id_funcionario)
        REFERENCES funcionario(id_funcionario),

    CONSTRAINT fk_movimentacao_usuario_retirada
        FOREIGN KEY (id_usuario_retirada)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_movimentacao_usuario_devolucao
        FOREIGN KEY (id_usuario_devolucao)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_movimentacao_status
        FOREIGN KEY (id_status_movimentacao)
        REFERENCES status_movimentacao(id_status_movimentacao)
);