ALTER TABLE usr
ADD COLUMN is_activated boolean default false AFTER has_avatar;