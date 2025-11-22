import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import keycloak from './keycloak';
import {
    TextField,
    Button,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Paper,
    Typography,
    Box
} from '@mui/material';

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
        <Paper sx={{ p: 3, maxWidth: 500, margin: 'auto' }}>
            <Typography variant="h4" gutterBottom>
                Create User
            </Typography>
            <form onSubmit={handleSubmit}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    <TextField
                        label="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                        fullWidth
                    />
                    <TextField
                        label="First Name"
                        value={firstName}
                        onChange={(e) => setFirstName(e.target.value)}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Last Name"
                        value={lastName}
                        onChange={(e) => setLastName(e.target.value)}
                        required
                        fullWidth
                    />
                    <TextField
                        label="Email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        fullWidth
                    />
                    <FormControl fullWidth>
                        <InputLabel>Role</InputLabel>
                        <Select
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                        >
                            <MenuItem value="student">Student</MenuItem>
                            <MenuItem value="teacher">Teacher</MenuItem>
                            <MenuItem value="admin">Admin</MenuItem>
                        </Select>
                    </FormControl>
                    <Button type="submit" variant="contained" color="primary" sx={{ mt: 2 }}>
                        Create User
                    </Button>
                </Box>
            </form>
        </Paper>
    );
};

export default UserForm;
