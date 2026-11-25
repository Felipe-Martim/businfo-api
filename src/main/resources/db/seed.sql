-- ============================================================================
-- BusInfo – Horários e Itinerários de Ônibus
-- A3 - Implementação da Primeira Versão
-- Script DML: Carga Inicial de Dados (Seed)
-- ============================================================================

-- 1. Inserção das Linhas de Ônibus (RF01, RF05, RF07)
-- Linha 1 corresponde exatamente ao exemplo da Especificação da API
INSERT INTO linhas (id, numero, nome, origem, destino) VALUES
(1, '101', 'Centro - Bairro São José', 'Terminal Central', 'Bairro São José'),
(2, '102', 'Centro - Jardim América', 'Terminal Central', 'Jardim América'),
(3, '204', 'Terminal Norte - Campus Universitário', 'Terminal Norte', 'Campus Universitário'),
(4, '305', 'Vila Nova - Hospital Regional', 'Terminal Vila Nova', 'Hospital Regional'),
(5, '410', 'Circular Expresso Sul - Centro', 'Terminal Sul', 'Terminal Central'),
(6, '502', 'Distrito Industrial - Estação Ferroviária', 'Distrito Industrial', 'Estação Ferroviária');

-- 2. Inserção dos Horários de Saída por Linha e Dia da Semana (RF02, RF06)
-- Linha 101 (Centro - Bairro São José)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('06:00', 'Dias Úteis', 1),
('07:00', 'Dias Úteis', 1),
('08:00', 'Dias Úteis', 1),
('09:00', 'Dias Úteis', 1),
('12:00', 'Dias Úteis', 1),
('14:00', 'Dias Úteis', 1),
('17:30', 'Dias Úteis', 1),
('19:00', 'Dias Úteis', 1),
('21:30', 'Dias Úteis', 1),
('07:00', 'Sábado', 1),
('09:00', 'Sábado', 1),
('12:00', 'Sábado', 1),
('16:00', 'Sábado', 1),
('20:00', 'Sábado', 1),
('08:00', 'Domingo', 1),
('12:00', 'Domingo', 1),
('18:00', 'Domingo', 1);

-- Linha 102 (Centro - Jardim América)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('05:45', 'Dias Úteis', 2),
('06:45', 'Dias Úteis', 2),
('07:45', 'Dias Úteis', 2),
('11:30', 'Dias Úteis', 2),
('15:00', 'Dias Úteis', 2),
('18:15', 'Dias Úteis', 2),
('22:00', 'Dias Úteis', 2),
('07:30', 'Sábado', 2),
('11:30', 'Sábado', 2),
('17:30', 'Sábado', 2),
('09:00', 'Domingo', 2),
('15:00', 'Domingo', 2);

-- Linha 204 (Terminal Norte - Campus Universitário)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('06:15', 'Dias Úteis', 3),
('06:50', 'Dias Úteis', 3),
('07:20', 'Dias Úteis', 3),
('12:10', 'Dias Úteis', 3),
('13:30', 'Dias Úteis', 3),
('17:50', 'Dias Úteis', 3),
('18:40', 'Dias Úteis', 3),
('22:30', 'Dias Úteis', 3),
('07:30', 'Sábado', 3),
('12:30', 'Sábado', 3);

-- Linha 305 (Vila Nova - Hospital Regional)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('05:30', 'Dias Úteis', 4),
('06:30', 'Dias Úteis', 4),
('08:30', 'Dias Úteis', 4),
('10:30', 'Dias Úteis', 4),
('14:30', 'Dias Úteis', 4),
('16:30', 'Dias Úteis', 4),
('18:30', 'Dias Úteis', 4),
('20:30', 'Dias Úteis', 4),
('06:30', 'Sábado', 4),
('10:30', 'Sábado', 4),
('14:30', 'Sábado', 4),
('18:30', 'Sábado', 4),
('07:00', 'Domingo', 4),
('13:00', 'Domingo', 4),
('19:00', 'Domingo', 4);

-- Linha 410 (Circular Expresso Sul - Centro)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('06:00', 'Dias Úteis', 5),
('06:30', 'Dias Úteis', 5),
('07:00', 'Dias Úteis', 5),
('07:30', 'Dias Úteis', 5),
('08:00', 'Dias Úteis', 5),
('17:00', 'Dias Úteis', 5),
('17:30', 'Dias Úteis', 5),
('18:00', 'Dias Úteis', 5),
('18:30', 'Dias Úteis', 5),
('08:00', 'Sábado', 5),
('12:00', 'Sábado', 5),
('18:00', 'Sábado', 5);

-- Linha 502 (Distrito Industrial - Estação Ferroviária)
INSERT INTO horarios (horario, dia_semana, linha_id) VALUES
('05:15', 'Dias Úteis', 6),
('06:00', 'Dias Úteis', 6),
('07:15', 'Dias Úteis', 6),
('12:00', 'Dias Úteis', 6),
('17:10', 'Dias Úteis', 6),
('18:10', 'Dias Úteis', 6),
('23:10', 'Dias Úteis', 6),
('06:00', 'Sábado', 6),
('14:00', 'Sábado', 6);

-- 3. Inserção dos Pontos de Parada / Itinerário (RF03, RF04)
-- Linha 101 (Exatamente conforme exemplo da Especificação da API + endereços)
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Terminal Central', 'Av. Presidente Vargas, 100 - Centro', 1, 1),
('Praça da Matriz', 'Praça da Matriz, s/n - Centro Histórico', 2, 1),
('Escola Municipal', 'Rua Sete de Setembro, 450 - Boa Vista', 3, 1),
('Bairro São José', 'Av. São José, 1200 - Bairro São José', 4, 1);

-- Linha 102
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Terminal Central', 'Av. Presidente Vargas, 100 - Centro', 1, 2),
('Mercado Público', 'Rua Quinze de Novembro, 230 - Centro', 2, 2),
('Parque das Águas', 'Av. Brasil, 890 - Parque das Águas', 3, 2),
('Shopping Cidade', 'Av. das Américas, 1500 - Jardim América', 4, 2),
('Jardim América', 'Rua Colômbia, 310 - Jardim América', 5, 2);

-- Linha 204
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Terminal Norte', 'Av. Tiradentes, 2100 - Zona Norte', 1, 3),
('Biblioteca Pública', 'Rua Dom Pedro II, 540 - Santa Cruz', 2, 3),
('Ginásio Poliesportivo', 'Av. dos Estudantes, 780 - Universitário', 3, 3),
('Hospital Universitário', 'Av. dos Estudantes, 1100 - Universitário', 4, 3),
('Campus Universitário', 'Bloco Central - Cidade Universitária', 5, 3);

-- Linha 305
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Terminal Vila Nova', 'Rua São Paulo, 50 - Vila Nova', 1, 4),
('Posto de Saúde Central', 'Av. Rio Branco, 610 - Bela Vista', 2, 4),
('Praça do Trabalhador', 'Praça do Trabalhador, 15 - Centro', 3, 4),
('Centro de Especialidades', 'Av. da Saudade, 900 - Saúde', 4, 4),
('Hospital Regional', 'Av. da Saúde, 1450 - Bairro Hospitalar', 5, 4);

-- Linha 410
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Terminal Sul', 'Rod. BR-101, Km 12 - Zona Sul', 1, 5),
('Viaduto Sul', 'Av. Integração, 400 - Cruzeiro', 2, 5),
('Estação Cidadania', 'Av. Independência, 980 - Centro Sul', 3, 5),
('Terminal Central', 'Av. Presidente Vargas, 100 - Centro', 4, 5);

-- Linha 502
INSERT INTO pontos_parada (nome, endereco, ordem, linha_id) VALUES
('Distrito Industrial', 'Av. das Indústrias, 3000 - Distrito Industrial', 1, 6),
('Trevo Oeste', 'Rod. Estadual, Km 4 - Zona Oeste', 2, 6),
('Vila Operária', 'Rua dos Metalúrgicos, 420 - Vila Operária', 3, 6),
('Estação Ferroviária', 'Praça da Estação, 10 - Centro', 4, 6);

-- 4. Inserção de Avisos sobre Alterações ou Interrupções (RF08)
INSERT INTO avisos (tipo, descricao, data_registro, ativo, linha_id) VALUES
('Operação Normal', 'Linha operando normalmente em todos os horários previstos.', '2026-10-18', 1, 1),
('Alteração de Trajeto', 'Desvio temporário na Rua Quinze de Novembro devido a obras de recapeamento asfáltico.', '2026-10-18', 1, 2),
('Reforço de Frota', 'Ônibus extras nos horários de pico (07:20 e 18:40) durante o período letivo.', '2026-10-18', 1, 3),
('Operação Normal', 'Atendimento normal com veículos adaptados para acessibilidade.', '2026-10-18', 1, 4),
('Interrupção Parcial', 'Parada Viaduto Sul temporariamente deslocada 100m à frente por manutenção na via.', '2026-10-18', 1, 5),
('Operação Normal', 'Horários sincronizados com a chegada dos trens na Estação Ferroviária.', '2026-10-18', 1, 6);
