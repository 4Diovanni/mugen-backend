-- Adicionar created_at e updated_at nas tabelas de relacionamento se não existirem

-- character_skill
ALTER TABLE character_skill
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- character_transformation
ALTER TABLE character_transformation
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- tp_transaction (se não tiver)
ALTER TABLE tp_transaction
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Trigger para updated_at em character_skill
CREATE OR REPLACE FUNCTION update_character_skill_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS character_skill_updated_at ON character_skill;
CREATE TRIGGER character_skill_updated_at
    BEFORE UPDATE ON character_skill
    FOR EACH ROW
    EXECUTE FUNCTION update_character_skill_updated_at();

-- Trigger para updated_at em character_transformation
CREATE OR REPLACE FUNCTION update_character_transformation_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS character_transformation_updated_at ON character_transformation;
CREATE TRIGGER character_transformation_updated_at
    BEFORE UPDATE ON character_transformation
    FOR EACH ROW
    EXECUTE FUNCTION update_character_transformation_updated_at();
