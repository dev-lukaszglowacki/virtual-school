import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Typography
} from '@mui/material';

const MyGrades = () => {
    const [grades, setGrades] = useState([]);

    useEffect(() => {
        const fetchGrades = async () => {
            try {
                const response = await fetch('/api/grades/student', {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    setGrades(data);
                } else {
                    console.error('Failed to fetch grades');
                }
            } catch (error) {
                console.error('Error fetching grades:', error);
            }
        };

        fetchGrades();
    }, []);

    return (
        <Paper sx={{ p: 2 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>My Grades</Typography>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Subject</TableCell>
                            <TableCell>Lecturer</TableCell>
                            <TableCell>Grade</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {grades.map(grade => (
                            <TableRow key={grade.id}>
                                <TableCell>{grade.subjectName}</TableCell>
                                <TableCell>{grade.lecturerName}</TableCell>
                                <TableCell>{grade.grade}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    );
};

export default MyGrades;
