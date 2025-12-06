-- MUGEN RPG - Migration V3: TP e Achievements
-- Criação de tabelas para Sistema de TP e Achievements

-- ==================== TP TRANSACTIONS ====================
CREATE TABLE IF NOT EXISTS tp_transaction (
                                              id BIGSERIAL NOT NULL,
                                              character_id UUID NOT NULL,
                                              amount INTEGER NOT NULL,
                                              balance_after INTEGER NOT NULL,
                                              reason VARCHAR(255) NOT NULL,
                                              transaction_type VARCHAR(50) NOT NULL,
                                              created_by UUID,
                                              metadata JSONB,
                                              created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                              updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                              PRIMARY KEY (id),
                                              CONSTRAINT fk_tp_transaction_character FOREIGN KEY (character_id)
                                                  REFERENCES character(id) ON DELETE CASCADE,
                                              CONSTRAINT fk_tp_transaction_created_by FOREIGN KEY (created_by)
                                                  REFERENCES app_user(id) ON DELETE SET NULL
);

-- ==================== ACHIEVEMENTS ====================
CREATE TABLE IF NOT EXISTS achievement (
                                           id SERIAL NOT NULL,
                                           key_name VARCHAR(120) NOT NULL UNIQUE,
                                           title VARCHAR(150) NOT NULL,
                                           description TEXT,
                                           requirement_json JSONB NOT NULL,
                                           reward_tp INTEGER DEFAULT 0,
                                           category VARCHAR(50),
                                           is_active BOOLEAN DEFAULT TRUE,
                                           created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                           PRIMARY KEY (id)
);

-- ==================== CHARACTER ACHIEVEMENTS ====================
CREATE TABLE IF NOT EXISTS character_achievement (
                                                     id BIGSERIAL NOT NULL,
                                                     character_id UUID NOT NULL,
                                                     achievement_id INTEGER NOT NULL,
                                                     obtained_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                     notification_message TEXT,
                                                     PRIMARY KEY (id),
                                                     CONSTRAINT fk_character_achievement_character FOREIGN KEY (character_id)
                                                         REFERENCES character(id) ON DELETE CASCADE,
                                                     CONSTRAINT fk_character_achievement_achievement FOREIGN KEY (achievement_id)
                                                         REFERENCES achievement(id) ON DELETE CASCADE,
                                                     CONSTRAINT uk_character_achievement UNIQUE (character_id, achievement_id)
);

-- ==================== EVENT LOG ====================
CREATE TABLE IF NOT EXISTS event_log (
                                         id BIGSERIAL NOT NULL,
                                         user_id UUID,
                                         character_id UUID,
                                         event_type VARCHAR(120) NOT NULL,
                                         event_action VARCHAR(255) NOT NULL,
                                         payload JSONB,
                                         created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                         PRIMARY KEY (id),
                                         CONSTRAINT fk_event_log_user FOREIGN KEY (user_id)
                                             REFERENCES app_user(id) ON DELETE SET NULL,
                                         CONSTRAINT fk_event_log_character FOREIGN KEY (character_id)
                                             REFERENCES character(id) ON DELETE SET NULL
);

-- ==================== CONFIG ====================
CREATE TABLE IF NOT EXISTS config (
                                      key VARCHAR(120) NOT NULL,
                                      value_json JSONB NOT NULL,
                                      description TEXT,
                                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (key)
);

-- ==================== INDEXES ====================
CREATE INDEX IF NOT EXISTS idx_tp_transaction_character ON tp_transaction(character_id);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_created_by ON tp_transaction(created_by);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_transaction_type ON tp_transaction(transaction_type);
CREATE INDEX IF NOT EXISTS idx_tp_transaction_created_at ON tp_transaction(created_at);

CREATE INDEX IF NOT EXISTS idx_achievement_category ON achievement(category);
CREATE INDEX IF NOT EXISTS idx_achievement_is_active ON achievement(is_active);
CREATE INDEX IF NOT EXISTS idx_achievement_key_name ON achievement(key_name);

CREATE INDEX IF NOT EXISTS idx_character_achievement_character ON character_achievement(character_id);
CREATE INDEX IF NOT EXISTS idx_character_achievement_achievement ON character_achievement(achievement_id);
CREATE INDEX IF NOT EXISTS idx_character_achievement_obtained_at ON character_achievement(obtained_at);

CREATE INDEX IF NOT EXISTS idx_event_log_user ON event_log(user_id);
CREATE INDEX IF NOT EXISTS idx_event_log_character ON event_log(character_id);
CREATE INDEX IF NOT EXISTS idx_event_log_event_type ON event_log(event_type);
CREATE INDEX IF NOT EXISTS idx_event_log_created_at ON event_log(created_at);

-- ==================== INITIAL DATA ====================
INSERT INTO achievement (key_name, title, description, requirement_json, reward_tp, category, is_active)
VALUES (
           'first_character',
           'Primeiro Personagem',
           'Crie seu primeiro personagem',
           '{"minCharacters": 1}',
           10,
           'SPECIAL',
           TRUE
       ) ON CONFLICT (key_name) DO NOTHING;

INSERT INTO config (key, value_json, description)
VALUES
    ('max_attribute_value', '{"value": 120}', 'Valor máximo por atributo'),
    ('starting_tp', '{"value": 10}', 'TP inicial ao criar personagem')
ON CONFLICT (key) DO NOTHING;