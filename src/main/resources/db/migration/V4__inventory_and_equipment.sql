-- ============================================
-- MUGEN RPG - Flyway Migration V4
-- Weapons, Armors, Materials, Inventory & Equipment
-- ============================================

-- ==================== WEAPONS ====================
CREATE TABLE IF NOT EXISTS weapon (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    notes TEXT,
    image_url VARCHAR(500),
    primary_type VARCHAR(50) NOT NULL DEFAULT 'ATAQUE',
    secondary_type VARCHAR(50) NOT NULL DEFAULT 'ESPADA',
    rarity VARCHAR(50) NOT NULL DEFAULT 'COMUM',
    elemental_type VARCHAR(50) DEFAULT 'NEUTRAL',
    tp_cost BIGINT NOT NULL DEFAULT 100,
    -- Attribute Bonuses
    str_bonus INT DEFAULT 0,
    dex_bonus INT DEFAULT 0,
    con_bonus INT DEFAULT 0,
    wil_bonus INT DEFAULT 0,
    mnd_bonus INT DEFAULT 0,
    spi_bonus INT DEFAULT 0,
    -- Combat Stats
    physical_damage INT DEFAULT 10 CHECK (physical_damage >= 0),
    magic_damage INT DEFAULT 0 CHECK (magic_damage >= 0),
    critical_chance INT DEFAULT 5 CHECK (critical_chance >= 0 AND critical_chance <= 100),
    -- Requirements
    min_level INT DEFAULT 1 CHECK (min_level >= 1),
    min_str INT DEFAULT 0 CHECK (min_str >= 0),
    min_dex INT DEFAULT 0 CHECK (min_dex >= 0),
    min_con INT DEFAULT 0 CHECK (min_con >= 0),
    min_wil INT DEFAULT 0 CHECK (min_wil >= 0),
    min_mnd INT DEFAULT 0 CHECK (min_mnd >= 0),
    min_spi INT DEFAULT 0 CHECK (min_spi >= 0),
    -- Control
    is_unique BOOLEAN DEFAULT FALSE,
    max_quantity INT DEFAULT 99,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO weapon (name, description, notes, primary_type, secondary_type, rarity, elemental_type, tp_cost, str_bonus, dex_bonus, physical_damage, min_level, min_str, min_dex, min_con, min_wil, min_mnd, min_spi) VALUES
    ('Espada de Ferro', 'Uma espada comum de ferro', 'Arma iniciante', 'ATAQUE', 'ESPADA', 'COMUM', 'NEUTRAL', 100, 1, 0, 10, 1, 1, 1, 1, 1, 1, 1),
    ('Espada Longa', 'Uma espada longa de aço', 'Arma de qualidade superior', 'ATAQUE', 'ESPADA', 'RARO', 'NEUTRAL', 150, 3, 2, 18, 3, 3, 2, 2, 1, 1, 1),
    ('Excalibur', 'Uma lendária espada com poder sagrado', 'Lendária espada divina', 'ATAQUE', 'ESPADA', 'LENDARIO', 'LIGHT', 300, 10, 8, 45, 15, 10, 8, 5, 5, 3, 3)
ON CONFLICT (name) DO NOTHING;

-- ==================== ARMORS ====================
CREATE TABLE IF NOT EXISTS armor (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    armor_type VARCHAR(50) NOT NULL DEFAULT 'LEVE',
    rarity VARCHAR(50) NOT NULL DEFAULT 'COMUM',
    tp_cost BIGINT NOT NULL DEFAULT 100,
    -- Attribute Bonuses
    str_bonus INT DEFAULT 0,
    con_bonus INT DEFAULT 0,
    dex_bonus INT DEFAULT 0,
    wil_bonus INT DEFAULT 0,
    mnd_bonus INT DEFAULT 0,
    spi_bonus INT DEFAULT 0,
    -- Defense Stats
    physical_defense INT DEFAULT 5 CHECK (physical_defense >= 0),
    magical_defense INT DEFAULT 3 CHECK (magical_defense >= 0),
    -- Requirements
    min_level INT DEFAULT 1,
    min_con INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO armor (name, description, armor_type, rarity, tp_cost, str_bonus, con_bonus, dex_bonus, min_level, min_con) VALUES
    ('Armadura de Pano', 'Uma simples armadura de pano', 'LEVE', 'COMUM', 50, 0, 1, 1, 1, 1),
    ('Armadura de Couro', 'Uma armadura de couro de qualidade', 'LEVE', 'INCOMUM', 150, 0, 2, 2, 2, 2),
    ('Armadura do Dragão', 'Uma lendária armadura feita com escamas de dragão', 'PESADO', 'LENDARIO', 1000, 5, 10, 2, 6, 20)
ON CONFLICT (name) DO NOTHING;

-- ==================== MATERIALS ====================
CREATE TABLE IF NOT EXISTS material (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(500),
    rarity VARCHAR(50) DEFAULT 'COMUM',
    value INT DEFAULT 10 CHECK (value >= 0),
    max_stack INT DEFAULT 99 CHECK (max_stack > 0),
    usage_type VARCHAR(50),
    added_by_master BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO material (name, description, rarity) VALUES
    ('Minério de Ferro', 'Um minério básico de ferro', 'COMUM'),
    ('Minério de Ouro', 'Um precioso minério de ouro', 'RARO'),
    ('Minério de Platina', 'Um raro e valioso minério de platina', 'EPICO'),
    ('Minério de Mithril', 'Um lendário minério mágico', 'LENDARIO'),
    ('Escama de Dragão Comum', 'Uma escama de um dragão comum', 'INCOMUM')
ON CONFLICT (name) DO NOTHING;

-- ==================== INVENTORY ====================
CREATE TABLE IF NOT EXISTS inventory (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID NOT NULL UNIQUE REFERENCES character(id) ON DELETE CASCADE,
    current_slots INT DEFAULT 0 CHECK (current_slots >= 0),
    max_slots INT DEFAULT 50 CHECK (max_slots > 0),
    total_value BIGINT DEFAULT 0 CHECK (total_value >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== INVENTORY WEAPONS ====================
CREATE TABLE IF NOT EXISTS inventory_weapon (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    weapon_id INT NOT NULL REFERENCES weapon(id) ON DELETE CASCADE,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    purchased_at_level INT DEFAULT 1 CHECK (purchased_at_level >= 1),
    acquired_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (inventory_id, weapon_id)
);

-- ==================== INVENTORY ARMORS ====================
CREATE TABLE IF NOT EXISTS inventory_armor (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    armor_id INT NOT NULL REFERENCES armor(id) ON DELETE CASCADE,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    purchased_at_level INT DEFAULT 1 CHECK (purchased_at_level >= 1),
    acquired_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (inventory_id, armor_id)
);

-- ==================== INVENTORY MATERIALS ====================
CREATE TABLE IF NOT EXISTS inventory_material (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id) ON DELETE CASCADE,
    material_id INT NOT NULL REFERENCES material(id) ON DELETE CASCADE,
    quantity INT DEFAULT 1 CHECK (quantity > 0),
    purchased_at_level INT DEFAULT 1 CHECK (purchased_at_level >= 1),
    acquired_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (inventory_id, material_id)
);

-- ==================== CHARACTER EQUIPMENT ====================
CREATE TABLE IF NOT EXISTS character_equipment (
    id BIGSERIAL PRIMARY KEY,
    character_id UUID NOT NULL UNIQUE REFERENCES character(id) ON DELETE CASCADE,
    weapon_id INT REFERENCES weapon(id) ON DELETE SET NULL,
    armor_id INT REFERENCES armor(id) ON DELETE SET NULL,
    status VARCHAR(50) DEFAULT 'NAO_EQUIPADO' NOT NULL,
    equipped_at TIMESTAMP WITH TIME ZONE,
    unequipped_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ==================== INDEXES ====================
CREATE INDEX IF NOT EXISTS idx_weapon_name ON weapon(name);
CREATE INDEX IF NOT EXISTS idx_weapon_primary_type ON weapon(primary_type);
CREATE INDEX IF NOT EXISTS idx_weapon_rarity ON weapon(rarity);
CREATE INDEX IF NOT EXISTS idx_weapon_is_active ON weapon(is_active);
CREATE INDEX IF NOT EXISTS idx_armor_name ON armor(name);
CREATE INDEX IF NOT EXISTS idx_armor_type ON armor(armor_type);
CREATE INDEX IF NOT EXISTS idx_armor_rarity ON armor(rarity);
CREATE INDEX IF NOT EXISTS idx_armor_is_active ON armor(is_active);
CREATE INDEX IF NOT EXISTS idx_material_name ON material(name);
CREATE INDEX IF NOT EXISTS idx_material_rarity ON material(rarity);
CREATE INDEX IF NOT EXISTS idx_inventory_character ON inventory(character_id);
CREATE INDEX IF NOT EXISTS idx_inventory_weapon_inventory ON inventory_weapon(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_weapon_weapon ON inventory_weapon(weapon_id);
CREATE INDEX IF NOT EXISTS idx_inventory_armor_inventory ON inventory_armor(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_armor_armor ON inventory_armor(armor_id);
CREATE INDEX IF NOT EXISTS idx_inventory_material_inventory ON inventory_material(inventory_id);
CREATE INDEX IF NOT EXISTS idx_inventory_material_material ON inventory_material(material_id);
CREATE INDEX IF NOT EXISTS idx_character_equipment_character ON character_equipment(character_id);
CREATE INDEX IF NOT EXISTS idx_character_equipment_weapon ON character_equipment(weapon_id);
CREATE INDEX IF NOT EXISTS idx_character_equipment_armor ON character_equipment(armor_id);

-- ==================== TRIGGERS ====================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_weapon_updated_at
    BEFORE UPDATE ON weapon
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_armor_updated_at
    BEFORE UPDATE ON armor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_material_updated_at
    BEFORE UPDATE ON material
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_updated_at
    BEFORE UPDATE ON inventory
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_weapon_updated_at
    BEFORE UPDATE ON inventory_weapon
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_armor_updated_at
    BEFORE UPDATE ON inventory_armor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_inventory_material_updated_at
    BEFORE UPDATE ON inventory_material
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_character_equipment_updated_at
    BEFORE UPDATE ON character_equipment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Inicializa inventory quando personagem é criado
CREATE OR REPLACE FUNCTION initialize_character_inventory()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO inventory (character_id, current_slots, max_slots, total_value)
    VALUES (NEW.id, 0, 50, 0);
    
    INSERT INTO character_equipment (character_id, status)
    VALUES (NEW.id, 'NAO_EQUIPADO');
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_initialize_character_inventory
    AFTER INSERT ON character
    FOR EACH ROW
    EXECUTE FUNCTION initialize_character_inventory();

-- ==================== COMMENTS ====================
COMMENT ON TABLE weapon IS 'Armas com bonuses de atributos e danos';
COMMENT ON TABLE armor IS 'Armaduras com bonuses de atributos e defesa';
COMMENT ON TABLE material IS 'Materiais para craft e trading - Gerenciados pelo MASTER';
COMMENT ON TABLE inventory IS 'Inventário principal do personagem';
COMMENT ON TABLE character_equipment IS 'Equipamento ativo/equipado do personagem';