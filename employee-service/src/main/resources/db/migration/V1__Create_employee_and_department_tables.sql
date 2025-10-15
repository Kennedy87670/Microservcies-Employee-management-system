-- Create departments table
CREATE TABLE departments (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(100) UNIQUE NOT NULL,
                             description TEXT,
                             manager_name VARCHAR(100),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create employees table
CREATE TABLE employees (
                           id BIGSERIAL PRIMARY KEY,
                           employee_id VARCHAR(20) UNIQUE NOT NULL,
                           first_name VARCHAR(50) NOT NULL,
                           last_name VARCHAR(50) NOT NULL,
                           email VARCHAR(100) UNIQUE NOT NULL,
                           phone VARCHAR(20),
                           position VARCHAR(100),
                           department_id BIGINT REFERENCES departments(id) ON DELETE SET NULL,
                           salary DECIMAL(10, 2),
                           status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                           hire_date DATE,
                           created_by VARCHAR(50),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_employee_department ON employees(department_id);
CREATE INDEX idx_employee_status ON employees(status);
CREATE INDEX idx_employee_email ON employees(email);