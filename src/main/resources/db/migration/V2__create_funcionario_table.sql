
/**
 * Author:  heito
 * Created: 15 de ago. de 2026
 */

CREATE TABLE funcionario (
    id_funcionario BIGINT AUTO_INCREMENT PRIMARY KEY,
    fk_setor BIGINT NOT NULL,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    data_admissao DATE,
    email VARCHAR(150),
    data_demissao DATE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_funcionario_setor
        FOREIGN KEY (fk_setor)
        REFERENCES setor(id_setor)
);
