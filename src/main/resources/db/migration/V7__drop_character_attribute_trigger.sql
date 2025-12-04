-- Remover trigger que cria atributos automaticamente
DROP TRIGGER IF EXISTS trigger_initialize_character_attributes ON character;
DROP FUNCTION IF EXISTS initialize_character_attributes();
