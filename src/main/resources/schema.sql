DROP TABLE IF EXISTS action_move CASCADE;

CREATE TABLE action_move (
                        id SERIAL PRIMARY KEY,
                        action_move_name VARCHAR(8),
                        speed INTEGER
);
