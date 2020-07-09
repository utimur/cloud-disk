ALTER TABLE file
ADD COLUMN is_favourite boolean default false;

ALTER TABLE usr
ADD COLUMN sent_activation_at DATETIME;