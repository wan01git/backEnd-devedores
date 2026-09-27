-- V1: estrutura inicial do banco de dados
-- Convenção Flyway: V{versao}__{descricao}.sql (dois underscores depois do numero)

-- usuarios: quem faz login no sistema
CREATE TABLE usuarios (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    senha_hash      VARCHAR(255) NOT NULL,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- pessoas: contatos do usuario com quem ele tem valores a pagar/receber
-- cada pessoa pertence a exatamente um usuario (FK usuario_id)
CREATE TABLE pessoas (
    id              BIGSERIAL PRIMARY KEY,
    usuario_id      BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    nome            VARCHAR(150) NOT NULL,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_pessoa_usuario_nome UNIQUE (usuario_id, nome)
);

-- indice na FK: postgres nao cria isso automaticamente, e toda consulta
-- "pessoas do usuario X" vai filtrar por essa coluna
CREATE INDEX idx_pessoas_usuario ON pessoas(usuario_id);

-- lancamentos: cada valor a pagar ou a receber, ligado a uma pessoa
CREATE TABLE lancamentos (
    id                  BIGSERIAL PRIMARY KEY,
    pessoa_id           BIGINT NOT NULL REFERENCES pessoas(id) ON DELETE CASCADE,
    tipo                VARCHAR(10) NOT NULL CHECK (tipo IN ('A_PAGAR', 'A_RECEBER')),
    valor               NUMERIC(12,2) NOT NULL CHECK (valor > 0),
    descricao           VARCHAR(255),
    data_lancamento     DATE NOT NULL DEFAULT CURRENT_DATE,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lancamentos_pessoa ON lancamentos(pessoa_id);
CREATE INDEX idx_lancamentos_data ON lancamentos(data_lancamento);
