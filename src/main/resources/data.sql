INSERT INTO persons (id, created_at, first_name, last_name, external_ref, birth_date)
VALUES ('00000000-0000-0000-0000-000000000001', NOW(), 'Maria', 'Ivanova', 'teacher-demo', '1980-03-01');

INSERT INTO persons (id, created_at, first_name, last_name, external_ref, birth_date)
VALUES ('00000000-0000-0000-0000-000000000002', NOW(), 'Alex', 'Petrov', 'student-demo', '2001-09-15');

INSERT INTO teachers (id, academic_title)
VALUES ('00000000-0000-0000-0000-000000000001', 'Professor');

INSERT INTO students (id, student_no, status)
VALUES ('00000000-0000-0000-0000-000000000002', 'STU-001', 'ACTIVE');

INSERT INTO courses (id, created_at, code, title, description, credits, author_id)
VALUES ('00000000-0000-0000-0000-000000000010', NOW(), 'ORM-DEMO', 'ORM Fundamentals', 'Demo course loaded from data.sql', 4,
        '00000000-0000-0000-0000-000000000001');

INSERT INTO tags (id, created_at, name, slug)
VALUES ('00000000-0000-0000-0000-000000000020', NOW(), 'Spring', 'spring');

INSERT INTO course_tags (course_id, tag_id)
VALUES ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000020');

INSERT INTO enrollments (id, created_at, role, enrolled_at, status, final_grade, student_id, course_id)
VALUES ('00000000-0000-0000-0000-000000000030', NOW(), 'STUDENT', NOW(), 'ENROLLED', NULL,
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000010');
