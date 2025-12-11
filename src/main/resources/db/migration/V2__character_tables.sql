-- ============================================
-- MUGEN RPG - Flyway Migration V2
-- Character Tables: Characters, Attributes, Skills
-- ============================================

-- ==================== CHARACTERS ====================
CREATE TABLE IF NOT EXISTS character (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name VARCHAR(120) NOT NULL,
    race_id INT NOT NULL REFERENCES race(id),
    level INT DEFAULT 1 CHECK (level >= 1 AND level <= 150),
    exp BIGINT DEFAULT 0 CHECK (exp >= 0),
    tp INT DEFAULT 10 CHECK (tp >= 0),
    active_transformation_id INT REFERENCES transformation(id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== CHARACTER ATTRIBUTES ====================
CREATE TABLE IF NOT EXISTS character_attribute (
    character_id UUID PRIMARY KEY REFERENCES character(id) ON DELETE CASCADE,
    str INT DEFAULT 0 CHECK (str >= 0 AND str <= 120),
    dex INT DEFAULT 0 CHECK (dex >= 0 AND dex <= 120),
    con INT DEFAULT 0 CHECK (con >= 0 AND con <= 120),
    wil INT DEFAULT 0 CHECK (wil >= 0 AND wil <= 120),
    mnd INT DEFAULT 0 CHECK (mnd >= 0 AND mnd <= 120),
    spi INT DEFAULT 0 CHECK (spi >= 0 AND spi <= 120),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== SKILLS ====================
CREATE TABLE IF NOT EXISTS skill (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    skill_type VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    base_tp_cost INT DEFAULT 10 CHECK (base_tp_cost >= 0),
    max_level INT DEFAULT 10 CHECK (max_level > 0),
    required_level INT DEFAULT 1 CHECK (required_level >= 1),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO skill (name, description, skill_type, base_tp_cost, max_level, required_level) VALUES
    ('Kamehameha', 'Onda de energia devastadora', 'ACTIVE', 10, 10, 1),
    ('Instant Transmission', 'Teletransporte instantâneo', 'PASSIVE', 15, 5, 5),
    ('Spirit Bomb', 'Esfera de energia espiritual', 'ACTIVE', 25, 10, 10),
    ('Solar Flare', 'Cegueira temporária', 'PASSIVE', 8, 5, 1),
    ('Destructo Disc', 'Disco cortante de energia', 'ACTIVE', 12, 10, 3),
    ('Special Beam Cannon', 'Canhão perfurante de energia', 'ACTIVE', 18, 10, 8),
    ('Masenko', 'Rajada de energia concentrada', 'ACTIVE', 10, 10, 2),
    ('Final Flash', 'Explosão final de energia', 'ACTIVE', 30, 10, 15),
    ('Flight', 'Capacidade de voar', 'PASSIVE', 5, 1, 1),
    ('Ki Sense', 'Sentir energia vital', 'PASSIVE', 5, 5, 1)
ON CONFLICT (name) DO NOTHING;

-- ==================== CHARACTER SKILLS ====================
CREATE TABLE IF NOT EXISTS character_skill (
    character_id UUID REFERENCES character(id) ON DELETE CASCADE,
    skill_id INT REFERENCES skill(id) ON DELETE CASCADE,
    current_level INT DEFAULT 1 CHECK (current_level >= 1),
    learned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (character_id, skill_id)
);

-- ==================== CHARACTER TRANSFORMATIONS ====================
CREATE TABLE IF NOT EXISTS character_transformation (
    character_id UUID REFERENCES character(id) ON DELETE CASCADE,
    transformation_id INT REFERENCES transformation(id) ON DELETE CASCADE,
    unlocked BOOLEAN DEFAULT FALSE,
    unlocked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (character_id, transformation_id)
);

-- ==================== INDEXES ====================
CREATE INDEX IF NOT EXISTS idx_character_owner ON character(owner_id);
CREATE INDEX IF NOT EXISTS idx_character_race ON character(race_id);
CREATE INDEX IF NOT EXISTS idx_character_is_active ON character(is_active);
CREATE INDEX IF NOT EXISTS idx_character_level ON character(level);
CREATE INDEX IF NOT EXISTS idx_skill_name ON skill(name);
CREATE INDEX IF NOT EXISTS idx_skill_type ON skill(skill_type);
CREATE INDEX IF NOT EXISTS idx_skill_is_active ON skill(is_active);
CREATE INDEX IF NOT EXISTS idx_character_skill_character ON character_skill(character_id);
CREATE INDEX IF NOT EXISTS idx_character_skill_skill ON character_skill(skill_id);
CREATE INDEX IF NOT EXISTS idx_character_transformation_character ON character_transformation(character_id);
CREATE INDEX IF NOT EXISTS idx_character_transformation_transformation ON character_transformation(transformation_id);

-- ==================== TRIGGERS ====================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_character_updated_at
    BEFORE UPDATE ON character
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_character_attribute_updated_at
    BEFORE UPDATE ON character_attribute
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_skill_updated_at
    BEFORE UPDATE ON skill
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_character_skill_updated_at
    BEFORE UPDATE ON character_skill
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_character_transformation_updated_at
    BEFORE UPDATE ON character_transformation
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ==================== INITIALIZATION TRIGGER ====================
-- Inicializa character_attribute quando um character é criado
CREATE OR REPLACE FUNCTION initialize_character_attributes()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO character_attribute (character_id, str, dex, con, wil, mnd, spi)
    SELECT
        NEW.id,
        r.start_str,
        r.start_dex,
        r.start_con,
        r.start_wil,
        r.start_mnd,
        r.start_spi
    FROM race r
    WHERE r.id = NEW.race_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_initialize_character_attributes
    AFTER INSERT ON character
    FOR EACH ROW
    EXECUTE FUNCTION initialize_character_attributes();

-- ==================== COMMENTS ====================
COMMENT ON TABLE character IS 'Personagens dos jogadores';
COMMENT ON TABLE character_attribute IS 'Atributos base de cada personagem';
COMMENT ON TABLE skill IS 'Habilidades disponíveis no sistema';
COMMENT ON TABLE character_skill IS 'Habilidades aprendidas por personagens';
COMMENT ON TABLE character_transformation IS 'Transformações desbloqueadas para personagens';