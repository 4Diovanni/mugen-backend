-- ============================================
-- MUGEN RPG - Migration V2: Characters
-- ============================================

-- Characters
CREATE TABLE IF NOT EXISTS character (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    owner_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    name VARCHAR(120) NOT NULL,
    race_id INT NOT NULL REFERENCES race(id),
    level INT DEFAULT 1 CHECK (level >= 1 AND level <= 150),
    exp BIGINT DEFAULT 0 CHECK (exp >= 0),
    tp INT DEFAULT 10 CHECK (tp >= 0),
    active_transformation_id INT REFERENCES transformation(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                                                       );

-- Character attributes
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

-- Skills
CREATE TABLE IF NOT EXISTS skill (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    skill_type VARCHAR(50) NOT NULL,
    base_tp_cost INT DEFAULT 10,
    max_level INT DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

INSERT INTO skill (name, skill_type, base_tp_cost, max_level) VALUES
                                                                  ('Kamehameha', 'ACTIVE', 10, 10),
                                                                  ('Flight', 'PASSIVE', 5, 1)
    ON CONFLICT DO NOTHING;

-- Character skills
CREATE TABLE IF NOT EXISTS character_skill (
                                               character_id UUID REFERENCES character(id) ON DELETE CASCADE,
    skill_id INT REFERENCES skill(id) ON DELETE CASCADE,
    current_level INT DEFAULT 1,
    learned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                                                                                              PRIMARY KEY (character_id, skill_id)
    );

-- Character transformations
CREATE TABLE IF NOT EXISTS character_transformation (
                                                        character_id UUID REFERENCES character(id) ON DELETE CASCADE,
    transformation_id INT REFERENCES transformation(id) ON DELETE CASCADE,
    unlocked BOOLEAN DEFAULT FALSE,
    unlocked_at TIMESTAMP WITH TIME ZONE,
                                                                                                       PRIMARY KEY (character_id, transformation_id)
    );

-- Indexes
CREATE INDEX IF NOT EXISTS idx_character_owner ON character(owner_id);
CREATE INDEX IF NOT EXISTS idx_character_race ON character(race_id);

-- Trigger para inicializar atributos
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

DROP TRIGGER IF EXISTS trigger_initialize_character_attributes ON character;
CREATE TRIGGER trigger_initialize_character_attributes
    AFTER INSERT ON character
    FOR EACH ROW
    EXECUTE FUNCTION initialize_character_attributes();
