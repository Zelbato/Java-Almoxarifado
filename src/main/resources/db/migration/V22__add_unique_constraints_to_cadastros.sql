ALTER TABLE setor
    ADD CONSTRAINT uk_setor_nome UNIQUE (nome);

ALTER TABLE categoria
    ADD CONSTRAINT uk_categoria_nome UNIQUE (nome);

ALTER TABLE localizacao
    ADD COLUMN corredor_chave_unicidade INT
        GENERATED ALWAYS AS (COALESCE(corredor, -1)) STORED,
    ADD COLUMN prateleira_chave_unicidade INT
        GENERATED ALWAYS AS (COALESCE(prateleira, -1)) STORED,
    ADD CONSTRAINT uk_localizacao_posicao
        UNIQUE (nome, corredor_chave_unicidade, prateleira_chave_unicidade);
