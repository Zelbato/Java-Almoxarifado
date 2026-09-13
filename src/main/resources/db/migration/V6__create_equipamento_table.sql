/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

CREATE TABLE equipamento (
    id_equipamento BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_localizacao BIGINT NOT NULL,
    id_categoria BIGINT NOT NULL,

    nome VARCHAR(150) NOT NULL,
    valor DECIMAL(10, 2),
    descricao VARCHAR(255),

    ativo BOOLEAN NOT NULL DEFAULT TRUE,

    marca VARCHAR(100),
    modelo VARCHAR(100),

    numero_patrimonio VARCHAR(100) UNIQUE,
    numero_serie VARCHAR(100),

    data_aquisicao DATE,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipamento_localizacao
        FOREIGN KEY (id_localizacao)
        REFERENCES localizacao(id_localizacao),

    CONSTRAINT fk_equipamento_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria)
);