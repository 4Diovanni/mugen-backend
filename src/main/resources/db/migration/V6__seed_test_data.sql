-- ============================================
-- SEED DATA: Dados de teste
-- ============================================

-- Roles (tabela é "roles", não "role")
INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
                                                                      (1, 'PLAYER', 'Jogador padrão', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                      (2, 'MASTER', 'Mestre do jogo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                      (3, 'ADMIN', 'Administrador do sistema', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ✅ Usuário de Teste com campos corretos (email, display_name, password_hash)
INSERT INTO app_user (id, email, display_name, password_hash, avatar_url, is_active, created_at, updated_at) VALUES
    (
        '123e4567-e89b-12d3-a456-426614174000',
        'player@mugenrpg.com',
        'Test Player',
        'password123',  -- Depois trocar por BCrypt hash
        NULL,
        true,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP
    )
ON CONFLICT (id) DO NOTHING;

-- Associar role ao usuário
INSERT INTO user_role (user_id, role_id) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 1)
ON CONFLICT DO NOTHING;

-- Raças padrão (Dragon Ball)
INSERT INTO race (id, name, start_str, start_dex, start_con, start_wil, start_mnd, start_spi, race_class_modifier, is_active, created_at, updated_at) VALUES
                                                                                                                                                          (1, 'Saiyan', 10, 8, 12, 9, 7, 8, 1.2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (2, 'Namekuseijin', 8, 9, 10, 12, 11, 13, 1.1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (3, 'Human', 9, 10, 9, 10, 10, 9, 1.0, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (4, 'Android', 11, 12, 14, 8, 6, 7, 1.15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (5, 'Majin', 7, 7, 13, 11, 9, 14, 1.25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (6, 'Arcosian', 9, 11, 10, 13, 8, 12, 1.3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                          (7, 'Half-Saiyan', 10, 9, 11, 11, 9, 10, 1.15, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Transformações padrão (multiplier como decimal: 1.5 = 1.5x, 2.0 = 2x)
INSERT INTO transformation (id, name, description, multiplier, required_level, race_required, race_id, is_active, created_at, updated_at) VALUES
-- Saiyan Transformations
(1, 'Super Saiyan', 'A lendária transformação dos Saiyans', 2.0, 10, true, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Super Saiyan 2', 'Evolução do Super Saiyan', 3.0, 25, true, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Super Saiyan 3', 'Terceiro estágio do Super Saiyan', 4.5, 40, true, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Super Saiyan God', 'Transformação divina dos Saiyans', 6.0, 60, true, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Namekuseijin Transformations
(5, 'Giant Form', 'Forma gigante dos Namekuseijins', 1.8, 15, true, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Red-Eyed Form', 'Forma de olhos vermelhos', 3.5, 30, true, 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Universal Transformations
(7, 'Kaioken', 'Técnica de multiplicação de poder', 1.5, 5, false, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Ultra Instinct', 'Estado de combate supremo', 10.0, 80, false, NULL, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;


-- ✅ Skills com campos corretos (name, description, skill_type, base_tp_cost, max_level)
INSERT INTO skill (id, name, description, skill_type, base_tp_cost, max_level, is_active, created_at, updated_at) VALUES
                                                                                                                      (1, 'Kamehameha', 'Onda de energia devastadora', 'OFFENSIVE', 10, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (2, 'Instant Transmission', 'Teletransporte instantâneo', 'UTILITY', 15, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (3, 'Spirit Bomb', 'Esfera de energia espiritual', 'OFFENSIVE', 25, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (4, 'Solar Flare', 'Cegueira temporária', 'UTILITY', 8, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (5, 'Destructo Disc', 'Disco cortante de energia', 'OFFENSIVE', 12, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (6, 'Special Beam Cannon', 'Canhão perfurante de energia', 'OFFENSIVE', 18, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (7, 'Masenko', 'Rajada de energia concentrada', 'OFFENSIVE', 10, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (8, 'Final Flash', 'Explosão final de energia', 'OFFENSIVE', 30, 10, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (9, 'Flight', 'Capacidade de voar', 'UTILITY', 5, 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                      (10, 'Ki Sense', 'Sentir energia vital', 'UTILITY', 5, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- ✅ Achievements com schema correto (key_name, title, requirement_json)
INSERT INTO achievement (id, key_name, title, description, requirement_json, reward_tp, category, is_active, created_at) VALUES
                                                                                                                             (1, 'FIRST_CHARACTER', 'First Steps', 'Criou seu primeiro personagem', '{"type": "CHARACTER_CREATED", "value": 1}'::jsonb, 5, 'PROGRESSION', true, CURRENT_TIMESTAMP),
                                                                                                                             (2, 'POWER_UP_50', 'Power Up', 'Alocou 50 pontos de atributo', '{"type": "ATTRIBUTE_ALLOCATED", "value": 50}'::jsonb, 10, 'PROGRESSION', true, CURRENT_TIMESTAMP),
                                                                                                                             (3, 'SKILL_MASTER', 'Skill Master', 'Aprendeu 5 habilidades', '{"type": "SKILLS_LEARNED", "value": 5}'::jsonb, 15, 'SKILLS', true, CURRENT_TIMESTAMP),
                                                                                                                             (4, 'FIRST_TRANSFORMATION', 'Transformation', 'Desbloqueou sua primeira transformação', '{"type": "TRANSFORMATION_UNLOCKED", "value": 1}'::jsonb, 20, 'TRANSFORMATIONS', true, CURRENT_TIMESTAMP),
                                                                                                                             (5, 'LEVEL_10', 'Level 10', 'Alcançou level 10', '{"type": "LEVEL_REACHED", "value": 10}'::jsonb, 25, 'PROGRESSION', true, CURRENT_TIMESTAMP),
                                                                                                                             (6, 'TP_HOARDER', 'TP Hoarder', 'Acumulou 100 TP', '{"type": "TP_ACCUMULATED", "value": 100}'::jsonb, 30, 'PROGRESSION', true, CURRENT_TIMESTAMP),
                                                                                                                             (7, 'SUPER_SAIYAN', 'Super Saiyan', 'Desbloqueou a transformação Super Saiyan', '{"type": "SPECIFIC_TRANSFORMATION", "transformationId": 1}'::jsonb, 50, 'TRANSFORMATIONS', true, CURRENT_TIMESTAMP),
                                                                                                                             (8, 'KAMEHAMEHA_MASTER', 'Kamehameha Master', 'Aprendeu o Kamehameha', '{"type": "SPECIFIC_SKILL", "skillId": 1}'::jsonb, 10, 'SKILLS', true, CURRENT_TIMESTAMP),
                                                                                                                             (9, 'FULL_POWER', 'Full Power', 'Alcançou todos os atributos em 50+', '{"type": "ALL_ATTRIBUTES_MIN", "value": 50}'::jsonb, 100, 'PROGRESSION', true, CURRENT_TIMESTAMP),
                                                                                                                             (10, 'LEGENDARY', 'Legendary Warrior', 'Alcançou level 50', '{"type": "LEVEL_REACHED", "value": 50}'::jsonb, 200, 'PROGRESSION', true, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Resetar sequências
SELECT setval('roles_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM roles), false);
SELECT setval('race_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM race), false);
SELECT setval('transformation_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM transformation), false);
SELECT setval('skill_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM skill), false);
SELECT setval('achievement_id_seq', (SELECT COALESCE(MAX(id), 0) + 1 FROM achievement), false);
