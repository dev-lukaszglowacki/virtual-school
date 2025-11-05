import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import keycloak from './keycloak';

const UserForm = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [role, setRole] = useState(location.state?.role || 'student');

    useEffect(() => {
        setRole(location.state?.role || 'student');
    }, [location.state]);

    const handleSubmit = (event) => {
        event.preventDefault();
        const user = { username, password, firstName, lastName, email, role };
        
        fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${keycloak.token}`
            },
            body: JSON.stringify(user)
        })
        .then(response => {
            if (response.ok) {
                alert('User created successfully');
                if (role === 'student') {
                    navigate('/students');
                } else if (role === 'teacher') {
                    navigate('/lecturers');
                } else {
                    navigate('/');
                }
            } else {
                alert('Failed to create user');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while creating the user');
        });
    };

    return (
        <div>
            <h2>Create User</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>Username:</label>
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} required />
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                </div>
                <div>
                    <label>First Name:</label>
                    <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                </div>
                <div>
                    <label>Last Name:</label>
                    <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                </div>
                <div>
                    <label>Email:</label>
                    <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div>
                    <label>Role:</label>
                    <select value={role} onChange={(e) => setRole(e.target.value)}>
                        <option value="student">Student</option>
                        <option value="teacher">Teacher</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
                <button type="submit">Create User</button>
            </form>
        </div>
    );
};

export default UserForm;
