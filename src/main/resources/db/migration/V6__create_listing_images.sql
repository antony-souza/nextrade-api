CREATE TABLE listing_images (
    id UUID PRIMARY KEY,
    listing_id UUID NOT NULL,
    image_url TEXT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6),
    deleted_at TIMESTAMP(6),
    CONSTRAINT fk_listing_images_listing FOREIGN KEY (listing_id) REFERENCES listings (id)
);

CREATE INDEX idx_listing_images_listing_id ON listing_images (listing_id);
