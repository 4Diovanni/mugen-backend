-- ============================================
-- MUGEN RPG - Flyway Migration V3
-- TP & Achievements System
-- ============================================

-- ==================== TP TRANSACTIONS ====================
CREATE TABLE IF NOT EXISTS tp_transaction (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_by UUID REFERENCES app_user(id) ON DELETE SET NULL,
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== ACHIEVEMENTS ====================
CREATE TABLE IF NOT EXISTS achievement (
    id SERIAL PRIMARY KEY,
    key_name VARCHAR(120) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    requirement_json JSONB NOT NULL,
    reward_tp INT DEFAULT 0 CHECK (reward_tp >= 0),
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO achievement (key_name, title, description, requirement_json, reward_tp, category, is_active) VALUES
    ('FIRST_CHARACTER', 'First Steps', 'Criou seu primeiro personagem', '{"type": "CHARACTER_CREATED", "value": 1}'::jsonb, 5, 'PROGRESSION', TRUE),
    ('POWER_UP_50', 'Power Up', 'Alocou 50 pontos de atributo', '{"type": "ATTRIBUTE_ALLOCATED", "value": 50}'::jsonb, 10, 'PROGRESSION', TRUE),
    ('SKILL_MASTER', 'Skill Master', 'Aprendeu 5 habilidades', '{"type": "SKILLS_LEARNED", "value": 5}'::jsonb, 15, 'SKILLS', TRUE),
    ('FIRST_TRANSFORMATION', 'Transformation', 'Desbloqueou sua primeira transformação', '{"type": "TRANSFORMATION_UNLOCKED", "value": 1}'::jsonb, 20, 'TRANSFORMATIONS', TRUE),
    ('LEVEL_10', 'Level 10', 'Alcançou level 10', '{"type": "LEVEL_REACHED", "value": 10}'::jsonb, 25, 'PROGRESSION', TRUE),
    ('TP_HOARDER', 'TP Hoarder', 'Acumulou 100 TP', '{"type": "TP_ACCUMULATED", "value": 100}'::jsonb, 30, 'PROGRESSION', TRUE),
    ('SUPER_SAIYAN', 'Super Saiyan', 'Desbloqueou a transformação Super Saiyan', '{"type": "SPECIFIC_TRANSFORMATION", "transformationId": 1}'::jsonb, 50, 'TRANSFORMATIONS', TRUE),
    ('KAMEHAMEHA_MASTER', 'Kamehameha Master', 'Aprendeu o Kamehameha', '{"type": "SPECIFIC_SKILL", "skillId": 1}'::jsonb, 10, 'SKILLS', TRUE),
    ('FULL_POWER', 'Full Power', 'Alcançou todos os atributos em 50+', '{"type": "ALL_ATTRIBUTES_MIN", "value": 50}'::jsonb, 100, 'PROGRESSION', TRUE),
    ('LEGENDARY', 'Legendary Warrior', 'Alcançou level 50', '{"type": "LEVEL_REACHED", "value": 50}'::jsonb, 200, 'PROGRESSION', TRUE)
ON CONFLICT (key_name) DO NOTHING;

-- ==================== CHARACTER ACHIEVEMENTS ====================
CREATE TABLE IF NOT EXISTS character_achievement (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    achievement_id INT NOT NULL REFERENCES achievement(id) ON DELETE CASCADE,
    obtained_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notification_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_character_achievement UNIQUE (character_id, achievement_id)
);

-- ==================== EVENT LOG ====================
CREATE TABLE IF NOT EXISTS event_log (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES app_user(id) ON DELETE SET NULL,
    character_id UUID REFERENCES character(id) ON DELETE SET NULL,
    event_type VARCHAR(120) NOT NULL,
    event_action VARCHAR(255) NOT NULL,
    payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== CONFIG ====================
CREATE TABLE IF NOT EXISTS config (
    key VARCHAR(120) PRIMARY KEY,
    value_json JSONB NOT NULL,
    description TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO config (key, value_json, description) VALUES
    ('max_attribute_value', '{"value": 120}'::jsonb, 'Valor máximo por atributo'),
    ('starting_tp', '{"value": 10}'::jsonb, 'TP inicial ao criar personagem')
ON CONFLICT (key) DO NOTHING;

-- ==================== INDEXES ====================
CREATE INDEX IF NOT EXISTS idx_tp_transaction_character ON tp_transaction(character_id);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_created_by ON tp_transaction(created_by);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_type ON tp_transaction(transaction_type);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_created_at ON tp_transaction(created_at);
CREATE INDEX IF NOT EXISTS idx_achievement_category ON achievement(category);
CREATE INDEX IF NOT EXISTS idx_achievement_is_active ON achievement(is_active);
CREATE INDEX IF NOT EXISTS idx_achievement_key_name ON achievement(key_name);
CREATE INDEX IF NOT EXISTS idx_character_achievement_character ON character_achievement(character_id);
CREATE INDEX IF NOT EXISTS idx_character_achievement_achievement ON character_achievement(achievement_id);
CREATE INDEX IF NOT EXISTS idx_character_achievement_obtained_at ON character_achievement(obtained_at);
CREATE INDEX IF NOT EXISTS idx_event_log_user ON event_log(user_id);
CREATE INDEX IF NOT EXISTS idx_event_log_character ON event_log(character_id);
CREATE INDEX IF NOT EXISTS idx_event_log_type ON event_log(event_type);
CREATE INDEX IF NOT EXISTS idx_event_log_created_at ON event_log(created_at);

-- ==================== TRIGGERS ====================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_tp_transaction_updated_at
    BEFORE UPDATE ON tp_transaction
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_achievement_updated_at
    BEFORE UPDATE ON achievement
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_character_achievement_updated_at
    BEFORE UPDATE ON character_achievement
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_config_updated_at
    BEFORE UPDATE ON config
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==================== COMMENTS ====================
COMMENT ON TABLE tp_transaction IS 'Histórico de transações de TP (TP Points)';
COMMENT ON TABLE achievement IS 'Achievements/conquistas do sistema';
COMMENT ON TABLE character_achievement IS 'Achievements desbloqueados por personagens';
COMMENT ON TABLE event_log IS 'Log de eventos do sistema';
COMMENT ON TABLE config IS 'Configurações globais do sistema';