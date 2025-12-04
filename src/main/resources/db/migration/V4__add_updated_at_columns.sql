-- ============================================
-- MUGEN RPG - Migration V4: Add updated_at and missing columns
-- ============================================

-- ============================================
-- ADICIONAR COLUNAS FALTANTES
-- ============================================

-- Adicionar required_level na tabela skill
ALTER TABLE skill
    ADD COLUMN IF NOT EXISTS required_level INTEGER DEFAULT 1;

-- Adicionar notes em transformation (se não existir)
ALTER TABLE transformation
    ADD COLUMN IF NOT EXISTS notes TEXT;

-- ============================================
-- ADICIONAR UPDATED_AT NAS TABELAS
-- ============================================

ALTER TABLE roles
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE race
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE transformation
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE skill
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE character
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE character_attribute
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE character_skill
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE character_transformation
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- ============================================
-- CRIAR FUNÇÃO PARA AUTO-UPDATE DO UPDATED_AT
-- ============================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- CRIAR TRIGGERS PARA TODAS AS TABELAS
-- ============================================

DROP TRIGGER IF EXISTS update_roles_updated_at ON roles;
CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE ON roles
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_race_updated_at ON race;
CREATE TRIGGER update_race_updated_at
    BEFORE UPDATE ON race
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_transformation_updated_at ON transformation;
CREATE TRIGGER update_transformation_updated_at
    BEFORE UPDATE ON transformation
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_skill_updated_at ON skill;
CREATE TRIGGER update_skill_updated_at
    BEFORE UPDATE ON skill
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_character_updated_at ON character;
CREATE TRIGGER update_character_updated_at
    BEFORE UPDATE ON character
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_character_attribute_updated_at ON character_attribute;
CREATE TRIGGER update_character_attribute_updated_at
    BEFORE UPDATE ON character_attribute
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_character_skill_updated_at ON character_skill;
CREATE TRIGGER update_character_skill_updated_at
    BEFORE UPDATE ON character_skill
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_character_transformation_updated_at ON character_transformation;
CREATE TRIGGER update_character_transformation_updated_at
    BEFORE UPDATE ON character_transformation
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_app_user_updated_at ON app_user;
CREATE TRIGGER update_app_user_updated_at
    BEFORE UPDATE ON app_user
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- COMENTÁRIOS PARA DOCUMENTAÇÃO
-- ============================================

COMMENT ON COLUMN skill.required_level IS 'Nível mínimo necessário para aprender a skill';
COMMENT ON COLUMN transformation.notes IS 'Observações adicionais sobre a transformação';
