/* ==========================================================================
   V1__initial_tables.sql
   ========================================================================== */

CREATE SCHEMA IF NOT EXISTS automatch_dev;

-- 1. TABELA CLASSIFIER (Referência para diversos enums do sistema)
CREATE TABLE classifier (
    id SERIAL PRIMARY KEY,
    type VARCHAR(100) NOT NULL,
    value VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- 2. TABELA ADDRESSES
CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    street VARCHAR(255),
    number VARCHAR(20),
    neighborhood VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 3. TABELA USERS
CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    phone VARCHAR(20),
    role TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    profile_image_url VARCHAR(255),
    address_id UUID REFERENCES addresses(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_loggin TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 4. TABELA INSTRUCTORS
CREATE TABLE instructors (
    user_id UUID PRIMARY KEY REFERENCES users(id),
    hourly_rate DECIMAL(10,2) NOT NULL,
    bio TEXT,
    years_experience INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    total_reviews INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 5. TABELA USER_DOCUMENTS
CREATE TABLE user_documents (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    document_type_id INT NOT NULL REFERENCES classifier(id),
    document_number VARCHAR(100) NOT NULL,
    document_image_url VARCHAR(255) NOT NULL,
    issue_date DATE,
    expiry_date DATE,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by_user_id UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    verification_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    UNIQUE (user_id, document_type_id),
    UNIQUE (document_number)
);
CREATE INDEX idx_user_docs_verified ON user_documents(is_verified);

-- 6. TABELA INSTRUCTOR_LICENSE_TYPES
CREATE TABLE instructor_license_types (
    id UUID PRIMARY KEY,
    instructor_id UUID REFERENCES instructors(user_id),
    license_type_id INT REFERENCES classifier(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (instructor_id, license_type_id)
);

-- 7. TABELA VEHICLES
CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    instructor_id UUID NOT NULL REFERENCES instructors(user_id),
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    model VARCHAR(100) NOT NULL,
    brand VARCHAR(100),
    year INTEGER,
    color VARCHAR(50),
    vehicle_image_url VARCHAR(255),
    transmission_type_id INT REFERENCES classifier(id),
    category_id INT REFERENCES classifier(id),
    has_dual_controls BOOLEAN DEFAULT TRUE,
    has_air_conditioning BOOLEAN DEFAULT TRUE,
    is_approved BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    last_maintenance_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_vehicles_instructor ON vehicles(instructor_id);
CREATE INDEX idx_vehicles_available ON vehicles(is_available);

-- 8. TABELA LESSONS
CREATE TABLE lessons (
    id UUID PRIMARY KEY,
    instructor_id UUID REFERENCES instructors(user_id),
    student_id UUID REFERENCES users(id),
    vehicle_id UUID REFERENCES vehicles(id),
    scheduled_at TIMESTAMP NOT NULL,
    duration_minutes INT,
    status_id INT REFERENCES classifier(id),
    address_id UUID REFERENCES addresses(id),
    price DECIMAL(10,2),
    payment_status_id INT REFERENCES classifier(id),
    payment_method_id INT REFERENCES classifier(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 9. TABELA REVIEWS
CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    lesson_id UUID UNIQUE REFERENCES lessons(id),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- 10. TABELA INSTRUCTOR_AVAILABILITY
CREATE TABLE instructor_availability (
    id UUID PRIMARY KEY,
    instructor_id UUID REFERENCES instructors(user_id),
    day_of_week INT NOT NULL, -- 0-6 ou 1-7
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT check_time_range CHECK (start_time < end_time)
);

-- 11. TABELA STUDENT_FAVORITES
CREATE TABLE student_favorites (
    id UUID PRIMARY KEY,
    student_id UUID REFERENCES users(id),
    instructor_id UUID REFERENCES instructors(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (student_id, instructor_id)
);

-- 12. TABELA PAYMENTS
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    lesson_id UUID UNIQUE REFERENCES lessons(id),
    amount DECIMAL(10,2) NOT NULL,
    status_id INT REFERENCES classifier(id),
    payment_method_id INT REFERENCES classifier(id),
    transaction_id VARCHAR(255) UNIQUE,
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

INSERT INTO classifier (type, value, description) VALUES
('USER_TYPE', 'INSTRUCTOR', 'Usuário do tipo Instrutor de direção'),
('USER_TYPE', 'STUDENT', 'Usuário do tipo Aluno/Aprendiz');