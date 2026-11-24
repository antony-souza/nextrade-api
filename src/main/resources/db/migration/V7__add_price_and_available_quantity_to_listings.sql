ALTER TABLE listings ADD COLUMN price NUMERIC(19, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE listings ADD COLUMN available_quantity INTEGER NOT NULL DEFAULT 0;

ALTER TABLE listings ALTER COLUMN price DROP DEFAULT;
ALTER TABLE listings ALTER COLUMN available_quantity DROP DEFAULT;

ALTER TABLE listings ADD CONSTRAINT ck_listings_price_nonnegative CHECK (price >= 0);
ALTER TABLE listings ADD CONSTRAINT ck_listings_available_quantity_nonnegative CHECK (available_quantity >= 0);
