CREATE TABLE listings (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    banner_url TEXT NOT NULL,
    description VARCHAR(255) NOT NULL,
    game_id UUID NOT NULL,
    category_id UUID NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT uk_listings_code UNIQUE (code),
    CONSTRAINT fk_listings_game FOREIGN KEY (game_id) REFERENCES games (id),
    CONSTRAINT fk_listings_category FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE INDEX idx_listings_game_id ON listings (game_id);
CREATE INDEX idx_listings_category_id ON listings (category_id);
