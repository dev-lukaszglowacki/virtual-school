import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import keycloak from './keycloak';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Button,
    Typography,
    Box
} from '@mui/material';

function Lecturers() {
    const [lecturers, setLecturers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const fetchLecturers = async () => {
        try {
            setLoading(true);
            const response = await fetch('/api/lecturers', {
                headers: {
                    Authorization: `Bearer ${keycloak.token}`,
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setLecturers(data);
        } catch (error) {
            setError(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchLecturers();
    }, []);

    const handleAddLecturer = () => {
        navigate('/create-user', { state: { role: 'teacher' } });
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;

    return (
        <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Lecturers</Typography>
                {keycloak.hasRealmRole('admin') && (
                    <Button variant="contained" onClick={handleAddLecturer}>Add Lecturer</Button>
                )}
            </Box>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>First Name</TableCell>
                            <TableCell>Last Name</TableCell>
                            <TableCell>Email</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {lecturers.map((lecturer) => (
                            <TableRow key={lecturer.id}>
                                <TableCell>{lecturer.id}</TableCell>
                                <TableCell>{lecturer.firstName}</TableCell>
                                <TableCell>{lecturer.lastName}</TableCell>
                                <TableCell>{lecturer.email}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    );
}

export default Lecturers;