-- Insert sample departments
INSERT INTO departments (name, description, manager_name) VALUES
                                                              ('Engineering', 'Software development and technical teams', 'John Smith'),
                                                              ('Human Resources', 'HR operations and recruitment', 'Sarah Johnson'),
                                                              ('Sales', 'Sales and business development', 'Mike Williams'),
                                                              ('Marketing', 'Marketing and communications', 'Emily Brown');

-- Insert sample employees
INSERT INTO employees (employee_id, first_name, last_name, email, phone, position, department_id, salary, status, hire_date, created_by) VALUES
                                                                                                                                             ('EMP001', 'Alice', 'Johnson', 'alice.johnson@company.com', '555-0101', 'Senior Developer', 1, 85000.00, 'ACTIVE', '2020-01-15', 'admin'),
                                                                                                                                             ('EMP002', 'Bob', 'Smith', 'bob.smith@company.com', '555-0102', 'HR Manager', 2, 75000.00, 'ACTIVE', '2019-03-20', 'admin'),
                                                                                                                                             ('EMP003', 'Carol', 'Davis', 'carol.davis@company.com', '555-0103', 'Sales Executive', 3, 70000.00, 'ACTIVE', '2021-06-10', 'admin'),
                                                                                                                                             ('EMP004', 'David', 'Wilson', 'david.wilson@company.com', '555-0104', 'Marketing Specialist', 4, 65000.00, 'ACTIVE', '2021-08-25', 'admin'),
                                                                                                                                             ('EMP005', 'Emma', 'Martinez', 'emma.martinez@company.com', '555-0105', 'Junior Developer', 1, 55000.00, 'ACTIVE', '2022-02-14', 'admin');