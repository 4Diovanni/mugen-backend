-- ============================================
-- MUGEN RPG - Migration V3: TP & Achievements
-- ============================================

-- TP Transactions
CREATE TABLE IF NOT EXISTS tp_transaction (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID NOT NULL REFERENCES character(id) ON DELETE CASCADE,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    reason VARCHAR(255) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_by UUID REFERENCES app_user(id),
    metadata JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                                                                                                      );

-- Achievements
CREATE TABLE IF NOT EXISTS achievement (
    id SERIAL PRIMARY KEY,
    key_name VARCHAR(120) UNIQUE NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    requirement_json JSONB NOT NULL,
    reward_tp INT DEFAULT 0,
    category VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO achievement (key_name, title, description, requirement_json, reward_tp, category) VALUES
    ('first_character', '🎮 Primeiro Personagem', 'Crie seu primeiro personagem', '{"min_characters": 1}', 10, 'SPECIAL')
    ON CONFLICT (key_name) DO NOTHING;

-- Character achievements
CREATE TABLE IF NOT EXISTS character_achievement (
    character_id UUID REFERENCES character(id) ON DELETE CASCADE,
    achievement_id INT REFERENCES achievement(id) ON DELETE CASCADE,
    obtained_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                                                                    PRIMARY KEY (character_id, achievement_id)
    );

-- Event log
CREATE TABLE IF NOT EXISTS event_log (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES app_user(id) ON DELETE SET NULL,
    character_id UUID REFERENCES character(id) ON DELETE SET NULL,
    event_type VARCHAR(120) NOT NULL,
    event_action VARCHAR(255) NOT NULL,
    payload JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

-- Config
CREATE TABLE IF NOT EXISTS config (
    key VARCHAR(120) PRIMARY KEY,
    value_json JSONB NOT NULL,
    description TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO config (key, value_json, description) VALUES
                                                      ('max_attribute_value', '120', 'Valor máximo por atributo'),
                                                      ('starting_tp', '10', 'TP inicial ao criar personagem')
    ON CONFLICT (key) DO NOTHING;

-- Indexes
CREATE INDEX IF NOT EXISTS idx_tp_transaction_character ON tp_transaction(character_id);
CREATE INDEX IF NOT EXISTS idx_event_log_user ON event_log(user_id);
CREATE INDEX IF NOT EXISTS idx_event_log_character ON event_log(character_id);
