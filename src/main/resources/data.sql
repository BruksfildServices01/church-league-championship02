-- ============================================
-- TIMES (IDs autogerados)  — upsert por SIGLA
-- ============================================
MERGE INTO team (name, sigla, captain, phone, status, notes) KEY (sigla)
VALUES
  ('Leões da Fé',        'LDF', 'João',   '11999999999', 'ATIVO', 'Time principal'),
  ('Águias do Reino',    'ADR', 'Marcos', '11888888888', 'ATIVO', 'Time jovem'),
  ('FJU Jaçanã',         'FJUJ','Lucas',  '11977777777', 'ATIVO', 'Time principal'),
  ('FJU Templo',         'TEM', 'Marcos', '11966666666', 'ATIVO', 'Time principal'),
  ('Guardiões Aliança',  'GDA', 'Rafael', '11955555555', 'ATIVO', 'Time cascudo'),
  ('Santos do Norte',    'SDN', 'Pedro',  '11944444444', 'ATIVO', 'Time aguerrido');

-- ============================================
-- JOGADORES (IDs autogerados) — upsert por (nome, time)
-- Formato MERGE ... SELECT (compatível H2) para ligar no team.id pela sigla
-- ============================================

-- LDF
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Leo',      t.id, 0 FROM team t WHERE t.sigla = 'LDF';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Lulu',     t.id, 0 FROM team t WHERE t.sigla = 'LDF';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Gabriel',  t.id, 0 FROM team t WHERE t.sigla = 'LDF';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Davi',     t.id, 0 FROM team t WHERE t.sigla = 'LDF';

-- ADR
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Gustavo',  t.id, 0 FROM team t WHERE t.sigla = 'ADR';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Thiago',   t.id, 0 FROM team t WHERE t.sigla = 'ADR';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Victor',   t.id, 0 FROM team t WHERE t.sigla = 'ADR';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Henrique', t.id, 0 FROM team t WHERE t.sigla = 'ADR';

-- FJUJ
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Vitor',    t.id, 0 FROM team t WHERE t.sigla = 'FJUJ';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Kaue',     t.id, 0 FROM team t WHERE t.sigla = 'FJUJ';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Matheus',  t.id, 0 FROM team t WHERE t.sigla = 'FJUJ';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'André',    t.id, 0 FROM team t WHERE t.sigla = 'FJUJ';

-- TEM
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Lucas Bezerra', t.id, 0 FROM team t WHERE t.sigla = 'TEM';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Pedro',         t.id, 0 FROM team t WHERE t.sigla = 'TEM';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Rafa',          t.id, 0 FROM team t WHERE t.sigla = 'TEM';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'João Victor',   t.id, 0 FROM team t WHERE t.sigla = 'TEM';

-- GDA
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Rafael',   t.id, 0 FROM team t WHERE t.sigla = 'GDA';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Samuel',   t.id, 0 FROM team t WHERE t.sigla = 'GDA';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Noah',     t.id, 0 FROM team t WHERE t.sigla = 'GDA';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Bruno',    t.id, 0 FROM team t WHERE t.sigla = 'GDA';

-- SDN
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Pedro',    t.id, 0 FROM team t WHERE t.sigla = 'SDN';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Luiz',     t.id, 0 FROM team t WHERE t.sigla = 'SDN';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Santos',   t.id, 0 FROM team t WHERE t.sigla = 'SDN';
MERGE INTO player (name, team_id, goals) KEY (name, team_id)
SELECT 'Icaro',    t.id, 0 FROM team t WHERE t.sigla = 'SDN';

-- ============================================
-- TORNEIOS (IDs autogerados) — upsert por NAME
-- ============================================
MERGE INTO tournament (name, type, has_return, points_win, points_draw, points_loss,
                       wo_home_goals, wo_away_goals, default_venue)
KEY (name)
VALUES
  ('Campeonato Interno 2025', 'PONTOS_CORRIDOS', TRUE, 3, 1, 0, 3, 0, 'Quadra da Sede'),
  ('Copa da Amizade 2025',    'PONTOS_CORRIDOS', FALSE, 3, 1, 0, 3, 0, 'Quadra B');

-- ============================================
-- INSCRIÇÕES — MERGE via SELECT (sem chutar ID)
-- ============================================

-- Campeonato Interno 2025: LDF, ADR, FJUJ, TEM
MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'LDF' WHERE t.name = 'Campeonato Interno 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'ADR' WHERE t.name = 'Campeonato Interno 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'FJUJ' WHERE t.name = 'Campeonato Interno 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'TEM' WHERE t.name = 'Campeonato Interno 2025';

-- Copa da Amizade 2025: GDA, SDN, LDF, ADR
MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'GDA' WHERE t.name = 'Copa da Amizade 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'SDN' WHERE t.name = 'Copa da Amizade 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'LDF' WHERE t.name = 'Copa da Amizade 2025';

MERGE INTO tournament_teams (tournament_id, team_id) KEY (tournament_id, team_id)
SELECT t.id, tm.id FROM tournament t JOIN team tm ON tm.sigla = 'ADR' WHERE t.name = 'Copa da Amizade 2025';
