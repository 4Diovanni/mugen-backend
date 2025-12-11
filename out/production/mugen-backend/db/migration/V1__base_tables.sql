-- ============================================
-- MUGEN RPG - Flyway Migration V1
-- Base Tables: Users, Roles, Races, Transformations
-- ============================================

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==================== ROLES ====================
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles (name, description) VALUES
    ('PLAYER', 'Jogador comum - pode gerenciar apenas seus personagens'),
    ('MASTER', 'Mestre do jogo - acesso total ao sistema'),
    ('ADMIN', 'Administrador do sistema')
ON CONFLICT (name) DO NOTHING;

-- ==================== USERS ====================
CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(120),
    password_hash TEXT,
    avatar_url TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== USER ROLES ====================
CREATE TABLE IF NOT EXISTS user_role (
    user_id UUID REFERENCES app_user(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

-- ==================== RACES ====================
CREATE TABLE IF NOT EXISTS race (
    id SERIAL PRIMARY KEY,
    name VARCHAR(80) UNIQUE NOT NULL,
    description TEXT,
    start_str INT DEFAULT 10 CHECK (start_str >= 0),
    start_dex INT DEFAULT 10 CHECK (start_dex >= 0),
    start_con INT DEFAULT 10 CHECK (start_con >= 0),
    start_wil INT DEFAULT 10 CHECK (start_wil >= 0),
    start_mnd INT DEFAULT 10 CHECK (start_mnd >= 0),
    start_spi INT DEFAULT 10 CHECK (start_spi >= 0),
    race_class_modifier NUMERIC(4,2) DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO race (name, description, start_str, start_dex, start_con, start_wil, start_mnd, start_spi, race_class_modifier) VALUES
    ('Saiyan', 'Guerreiros com grande potencial', 12, 10, 10, 14, 8, 10, 1.10),
    ('Namekuseijin', 'Seres com alta regeneração', 8, 10, 12, 10, 14, 16, 1.00),
    ('Humano', 'Versáteis e equilibrados', 10, 10, 10, 10, 10, 10, 1.05),
    ('Android', 'Guerreiros artificiais', 14, 12, 14, 8, 6, 7, 1.15),
    ('Majin', 'Criaturas poderosas', 7, 7, 13, 11, 9, 14, 1.25),
    ('Arcosian', 'Raça imperial', 9, 11, 10, 13, 8, 12, 1.30),
    ('Half-Saiyan', 'Híbrido Saiyan', 10, 9, 11, 11, 9, 10, 1.15)
ON CONFLICT (name) DO NOTHING;

-- ==================== TRANSFORMATIONS ====================
CREATE TABLE IF NOT EXISTS transformation (
    id SERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    race_id INT REFERENCES race(id) ON DELETE SET NULL,
    race_required BOOLEAN DEFAULT FALSE,
    required_level INT DEFAULT 0 CHECK (required_level >= 0),
    multiplier NUMERIC(5,3) NOT NULL CHECK (multiplier > 0),
    ki_drain_per_sec NUMERIC(6,3) DEFAULT 0,
    tp_cost INT DEFAULT 0,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO transformation (name, description, race_id, race_required, required_level, multiplier, ki_drain_per_sec, tp_cost) VALUES
    -- Saiyan Transformations
    ('Super Saiyan', 'A lendária transformação dos Saiyans', 1, TRUE, 10, 2.0, 3.5, 50),
    ('Super Saiyan 2', 'Evolução do Super Saiyan', 1, TRUE, 25, 3.0, 4.5, 75),
    ('Super Saiyan 3', 'Terceiro estágio do Super Saiyan', 1, TRUE, 40, 4.5, 6.0, 100),
    ('Super Saiyan God', 'Transformação divina dos Saiyans', 1, TRUE, 60, 6.0, 8.0, 150),
    -- Namekuseijin Transformations
    ('Giant Form', 'Forma gigante dos Namekuseijins', 2, TRUE, 15, 1.8, 2.0, 40),
    ('Red-Eyed Form', 'Forma de olhos vermelhos', 2, TRUE, 30, 3.5, 4.0, 80),
    -- Universal Transformations
    ('Kaioken', 'Técnica de multiplicação de poder', NULL, FALSE, 5, 1.5, 5.5, 30),
    ('Ultra Instinct', 'Estado de combate supremo', NULL, FALSE, 80, 10.0, 10.0, 200)
ON CONFLICT DO NOTHING;

-- ==================== INDEXES ====================
CREATE INDEX IF NOT EXISTS idx_app_user_email ON app_user(email);
CREATE INDEX IF NOT EXISTS idx_app_user_is_active ON app_user(is_active);
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON user_role(role_id);
CREATE INDEX IF NOT EXISTS idx_race_is_active ON race(is_active);
CREATE INDEX IF NOT EXISTS idx_transformation_race_id ON transformation(race_id);
CREATE INDEX IF NOT EXISTS idx_transformation_is_active ON transformation(is_active);

-- ==================== TRIGGERS ====================
-- Auto-update updated_at para tabelas
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_race_updated_at
    BEFORE UPDATE ON race
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transformation_updated_at
    BEFORE UPDATE ON transformation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_app_user_updated_at
    BEFORE UPDATE ON app_user
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==================== COMMENTS ====================
COMMENT ON TABLE roles IS 'Papéis e permissões de usuários';
COMMENT ON TABLE app_user IS 'Usuários do sistema';
COMMENT ON TABLE user_role IS 'Relacionamento entre usuários e papéis';
COMMENT ON TABLE race IS 'Raças disponíveis para criar personagens';
COMMENT ON TABLE transformation IS 'Transformações disponíveis para personagens';