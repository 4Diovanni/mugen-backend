-- ============================================
-- MUGEN RPG - Migration V1: Base Tables
-- ============================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Roles
CREATE TABLE IF NOT EXISTS roles (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO roles (name, description) VALUES
                                          ('PLAYER', 'Jogador comum - pode gerenciar apenas seus personagens'),
                                          ('MASTER', 'Mestre do jogo - acesso total ao sistema')
    ON CONFLICT (name) DO NOTHING;

-- Users
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

-- User roles
CREATE TABLE IF NOT EXISTS user_role (
                                         user_id UUID REFERENCES app_user(id) ON DELETE CASCADE,
    role_id INT REFERENCES roles(id) ON DELETE CASCADE,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                                                  PRIMARY KEY (user_id, role_id)
    );

CREATE INDEX IF NOT EXISTS idx_app_user_email ON app_user(email);
CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON user_role(user_id);

-- Races
CREATE TABLE IF NOT EXISTS race (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(80) UNIQUE NOT NULL,
    description TEXT,
    start_str INT DEFAULT 10,
    start_dex INT DEFAULT 10,
    start_con INT DEFAULT 10,
    start_wil INT DEFAULT 10,
    start_mnd INT DEFAULT 10,
    start_spi INT DEFAULT 10,
    race_class_modifier NUMERIC(4,2) DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO race (name, description, start_str, start_dex, start_con, start_wil, start_mnd, start_spi, race_class_modifier) VALUES
                                                                                                                                ('Saiyan', 'Guerreiros com grande potencial', 12, 10, 10, 14, 8, 10, 1.10),
                                                                                                                                ('Namekuseijin', 'Seres com alta regeneração', 8, 10, 12, 10, 14, 16, 1.00),
                                                                                                                                ('Humano', 'Versáteis e equilibrados', 10, 10, 10, 10, 10, 10, 1.05)
    ON CONFLICT (name) DO NOTHING;

-- Transformations
CREATE TABLE IF NOT EXISTS transformation (
                                              id SERIAL PRIMARY KEY,
                                              name VARCHAR(120) NOT NULL,
    description TEXT,
    race_id INT REFERENCES race(id),
    race_required BOOLEAN DEFAULT FALSE,
    required_level INT DEFAULT 0,
    multiplier NUMERIC(5,3) NOT NULL,
    ki_drain_per_sec NUMERIC(6,3) DEFAULT 0,
    tp_cost INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO transformation (name, race_id, race_required, required_level, multiplier, ki_drain_per_sec, tp_cost) VALUES
                                                                                                                     ('Super Saiyan', 1, TRUE, 15, 1.500, 3.5, 50),
                                                                                                                     ('Kaioken x2', NULL, FALSE, 10, 1.200, 5.5, 30)
    ON CONFLICT DO NOTHING;
