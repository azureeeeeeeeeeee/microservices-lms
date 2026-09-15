-- Make score nullable (submissions start ungraded)
ALTER TABLE submissions ALTER COLUMN score DROP NOT NULL;

-- Add content column for student's submission text (use default first, then drop it)
ALTER TABLE submissions ADD COLUMN content TEXT NOT NULL DEFAULT '';
ALTER TABLE submissions ALTER COLUMN content DROP DEFAULT;
