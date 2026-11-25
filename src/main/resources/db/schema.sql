-- ============================================================================
-- BusInfo – Horários e Itinerários de Ônibus
-- A3 - Implementação da Primeira Versão
-- Script DDL: Criação das Tabelas do Banco de Dados Relacional (SQLite)
-- ============================================================================

PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS avisos;
DROP TABLE IF EXISTS pontos_parada;
DROP TABLE IF EXISTS horarios;
DROP TABLE IF EXISTS linhas;

-- 1. Entidade Linha (Conforme Modelo Conceitual - Seção 6 da Especificação)
CREATE TABLE linhas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    numero VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    origem VARCHAR(120) NOT NULL,
    destino VARCHAR(120) NOT NULL
);

-- 2. Entidade Horario (Relacionamento 1:N com Linha)
CREATE TABLE horarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    horario VARCHAR(10) NOT NULL,
    dia_semana VARCHAR(40) NOT NULL,
    linha_id INTEGER NOT NULL,
    FOREIGN KEY (linha_id) REFERENCES linhas(id) ON DELETE CASCADE
);

-- 3. Entidade PontoParada (Relacionamento 1:N com Linha)
CREATE TABLE pontos_parada (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(120) NOT NULL,
    endereco VARCHAR(200) NOT NULL,
    ordem INTEGER NOT NULL DEFAULT 1,
    linha_id INTEGER NOT NULL,
    FOREIGN KEY (linha_id) REFERENCES linhas(id) ON DELETE CASCADE
);

-- 4. Entidade Aviso / Alteração na Linha (Atende ao RF08)
CREATE TABLE avisos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo VARCHAR(40) NOT NULL,
    descricao TEXT NOT NULL,
    data_registro VARCHAR(20) NOT NULL,
    ativo INTEGER NOT NULL DEFAULT 1,
    linha_id INTEGER NOT NULL,
    FOREIGN KEY (linha_id) REFERENCES linhas(id) ON DELETE CASCADE
);

-- Índices para garantir desempenho e tempo de resposta < 2s (RNF01)
CREATE INDEX idx_linhas_numero ON linhas(numero);
CREATE INDEX idx_linhas_nome ON linhas(nome);
CREATE INDEX idx_horarios_linha_id ON horarios(linha_id);
CREATE INDEX idx_horarios_dia_semana ON horarios(dia_semana);
CREATE INDEX idx_pontos_linha_id ON pontos_parada(linha_id);
CREATE INDEX idx_avisos_linha_id ON avisos(linha_id);
