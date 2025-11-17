-- базовые участники
INSERT INTO persons (id, created_at, first_name, last_name, external_ref, birth_date)
VALUES ('00000000-0000-0000-0000-000000000001', NOW(), 'Maria', 'Ivanova', 'teacher-demo', '1980-03-01');

INSERT INTO persons (id, created_at, first_name, last_name, external_ref, birth_date)
VALUES ('00000000-0000-0000-0000-000000000002', NOW(), 'Alex', 'Petrov', 'student-demo', '2001-09-15');

INSERT INTO teachers (id, academic_title)
VALUES ('00000000-0000-0000-0000-000000000001', 'Professor');

INSERT INTO students (id, student_no, status)
VALUES ('00000000-0000-0000-0000-000000000002', 'STU-001', 'ACTIVE');

-- учебные ресурсы
INSERT INTO learning_resources (id, created_at, title, complexity, resource_type, content_url, reading_time_min, video_url, duration_sec, transcript_url)
VALUES ('00000000-0000-0000-0000-000000000100', NOW(), 'Hibernate Cheatsheet', 'INTERMEDIATE', 'ARTICLE',
        'https://example.org/resources/hibernate-cheatsheet', 15, NULL, NULL, NULL);

INSERT INTO learning_resources (id, created_at, title, complexity, resource_type, content_url, reading_time_min, video_url, duration_sec, transcript_url)
VALUES ('00000000-0000-0000-0000-000000000101', NOW(), 'ORM Kickoff Video', 'BEGINNER', 'VIDEO',
        NULL, NULL, 'https://videos.example.org/orm-kickoff', 900, 'https://videos.example.org/orm-kickoff/transcript');

-- курс, теги и ключевые слова
INSERT INTO courses (id, created_at, code, title, description, credits, author_id, featured_resource_id)
VALUES ('00000000-0000-0000-0000-000000000010', NOW(), 'ORM-DEMO', 'ORM Fundamentals',
        'Демонстрационный курс по JPA/Hibernate с уроками, заданиями и викториной.', 4,
        '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000101');

INSERT INTO course_keywords (course_id, keyword)
VALUES ('00000000-0000-0000-0000-000000000010', 'orm'),
       ('00000000-0000-0000-0000-000000000010', 'hibernate');

INSERT INTO tags (id, created_at, name, slug)
VALUES ('00000000-0000-0000-0000-000000000020', NOW(), 'Spring', 'spring');

INSERT INTO course_tags (course_id, tag_id)
VALUES ('00000000-0000-0000-0000-000000000010', '00000000-0000-0000-0000-000000000020');

-- уроки и материалы
INSERT INTO lessons (id, created_at, title, syllabus, course_id, primary_resource_id, lesson_order)
VALUES ('00000000-0000-0000-0000-000000000200', NOW(), 'Введение в ORM',
        'История ORM и обзор Hibernate SessionFactory -> EntityManager.', '00000000-0000-0000-0000-000000000010',
        '00000000-0000-0000-0000-000000000101', 0),
       ('00000000-0000-0000-0000-000000000201', NOW(), 'Маппинг сущностей',
        'Аннотации @Entity, @Table, связи и каскады.', '00000000-0000-0000-0000-000000000010',
        NULL, 1);

INSERT INTO lesson_resources (lesson_id, resource_order, link)
VALUES ('00000000-0000-0000-0000-000000000200', 0, 'https://docs.spring.io/spring-data/jpa/docs/current/reference/html/'),
       ('00000000-0000-0000-0000-000000000201', 0, 'https://hibernate.org/orm/documentation/');

-- задание и пример сдачи
INSERT INTO assignments (id, created_at, title, due_date, max_points, course_id)
VALUES ('00000000-0000-0000-0000-000000000300', NOW(), 'HW #1 — модель данных',
        NOW() + INTERVAL '7 DAY', 100, '00000000-0000-0000-0000-000000000010');

INSERT INTO submissions (id, created_at, submitted_at, content_url, status, attempt_no, version, assignment_id, student_id)
VALUES ('00000000-0000-0000-0000-000000000310', NOW(), NOW(),
        'https://storage.example.org/submissions/hw1.pdf', 'GRADED', 1, 0,
        '00000000-0000-0000-0000-000000000300', '00000000-0000-0000-0000-000000000002');

INSERT INTO grades (id, created_at, points, graded_at, comment, version)
VALUES ('00000000-0000-0000-0000-000000000310', NOW(), 95.0, NOW(),
        'Отличное задание, хорошо проработаны связи.', 0);

-- викторина, вопросы и варианты ответов
INSERT INTO quizzes (id, created_at, title, description, time_limit_minutes, course_id)
VALUES ('00000000-0000-0000-0000-000000000400', NOW(), 'Quiz #1 — основы ORM',
        'Проверяем понимание ленивой загрузки и аннотаций.', 15, '00000000-0000-0000-0000-000000000010');

INSERT INTO questions (id, created_at, text, type, quiz_id)
VALUES ('00000000-0000-0000-0000-000000000401', NOW(), 'Что обозначает аббревиатура ORM?', 'SINGLE_CHOICE',
        '00000000-0000-0000-0000-000000000400'),
       ('00000000-0000-0000-0000-000000000402', NOW(), 'Выберите лениво загружаемые связи в JPA по умолчанию.', 'MULTIPLE_CHOICE',
        '00000000-0000-0000-0000-000000000400');

INSERT INTO answer_options (id, created_at, text, is_correct, question_id)
VALUES ('00000000-0000-0000-0000-000000000410', NOW(), 'Object Relational Mapping', true,
        '00000000-0000-0000-0000-000000000401'),
       ('00000000-0000-0000-0000-000000000411', NOW(), 'Official Resource Manual', false,
        '00000000-0000-0000-0000-000000000401'),
       ('00000000-0000-0000-0000-000000000412', NOW(), '@OneToMany', true,
        '00000000-0000-0000-0000-000000000402'),
       ('00000000-0000-0000-0000-000000000413', NOW(), '@ManyToMany', true,
        '00000000-0000-0000-0000-000000000402'),
       ('00000000-0000-0000-0000-000000000414', NOW(), '@ManyToOne', false,
        '00000000-0000-0000-0000-000000000402');

INSERT INTO quiz_submissions (id, created_at, score, attempt_no, taken_at, quiz_id, student_id)
VALUES ('00000000-0000-0000-0000-000000000420', NOW(), 100.0, 1, NOW(),
        '00000000-0000-0000-0000-000000000400', '00000000-0000-0000-0000-000000000002');

INSERT INTO quiz_submission_answers (submission_id, answer_option_id)
VALUES ('00000000-0000-0000-0000-000000000420', '00000000-0000-0000-0000-000000000410'),
       ('00000000-0000-0000-0000-000000000420', '00000000-0000-0000-0000-000000000412'),
       ('00000000-0000-0000-0000-000000000420', '00000000-0000-0000-0000-000000000413');

-- отзыв о курсе
INSERT INTO course_reviews (id, created_at, rating, comment, course_id, student_id)
VALUES ('00000000-0000-0000-0000-000000000500', NOW(), 5,
        'Курс загружается с готовыми уроками, легко начать проверку.', '00000000-0000-0000-0000-000000000010',
        '00000000-0000-0000-0000-000000000002');

-- зачисление для тестов
INSERT INTO enrollments (id, created_at, role, enrolled_at, status, final_grade, student_id, course_id)
VALUES ('00000000-0000-0000-0000-000000000030', NOW(), 'STUDENT', NOW(), 'ENROLLED', 95.0,
        '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000010');
