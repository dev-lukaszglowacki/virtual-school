import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
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
    Box,
    Select,
    MenuItem,
    FormControl,
    InputLabel
} from '@mui/material';

const ClassGrades = () => {
    const { subjectId } = useParams();
    const [users, setUsers] = useState([]);
    const [grades, setGrades] = useState({});
    const [newGrades, setNewGrades] = useState({});
    const [subjectName, setSubjectName] = useState('');


    useEffect(() => {
        const fetchSubjectDetails = async () => {
            try {
                const response = await fetch(`/api/subjects/${subjectId}`, {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if(response.ok) {
                    const data = await response.json();
                    setSubjectName(data.name);
                } else {
                    console.error('Failed to fetch subject details');
                }
            } catch (error) {
                console.error('Error fetching subject details:', error);
            }
        };

        const fetchStudents = async () => {
            try {
                const response = await fetch(`/api/subjects/${subjectId}/students`, {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    setStudents(data);
                } else {
                    console.error('Failed to fetch students');
                }
            } catch (error) {
                console.error('Error fetching subject details:', error);
            }
        };

        const fetchUsers = async () => {
            try {
                const response = await fetch(`/api/subjects/${subjectId}/users`, {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    setUsers(data);
                } else {
                    console.error('Failed to fetch users');
                }
            } catch (error) {
                console.error('Error fetching users:', error);
            }
        };

        const fetchGrades = async () => {
            try {
                const response = await fetch(`/api/grades/subject/${subjectId}`, {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    const gradesByStudent = data.reduce((acc, grade) => {
                        if (!acc[grade.studentId]) {
                            acc[grade.studentId] = [];
                        }
                        acc[grade.studentId].push(grade);
                        return acc;
                    }, {});
                    setGrades(gradesByStudent);
                } else {
                    console.error('Failed to fetch grades');
                }
            } catch (error) {
                console.error('Error fetching grades:', error);
            }
        };

        fetchSubjectDetails();
        fetchStudents();
        fetchGrades();
    }, [subjectId]);

    const handleGradeChange = (studentId, value) => {
        setNewGrades({
            ...newGrades,
            [studentId]: value
        });
    };

    const handleAddGrade = async (studentId) => {
        try {
            const response = await fetch('/api/grades', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${keycloak.token}`
                },
                body: JSON.stringify({
                    studentId: studentId,
                    subjectId: subjectId,
                    grade: newGrades[studentId]
                })
            });
            if (response.ok) {
                const newGrade = await response.json();
                setGrades({
                    ...grades,
                    [studentId]: [...(grades[studentId] || []), newGrade]
                });
                setNewGrades({
                    ...newGrades,
                    [studentId]: ''
                });
            } else {
                console.error('Failed to add grade');
            }
        } catch (error) {
            console.error('Error adding grade:', error);
        }
    };

    return (
        <Paper sx={{ p: 2 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>Grades for {subjectName}</Typography>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Student</TableCell>
                            <TableCell>Grades</TableCell>
                            <TableCell>New Grade</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {students.map(student => (
                            <TableRow key={student.id}>
                                <TableCell>{student.firstName} {student.lastName}</TableCell>
                                <TableCell>
                                    {grades[student.id] && grades[student.id].map(grade => grade.grade).join(', ')}
                                </TableCell>
                                <TableCell>
                                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <FormControl size="small" sx={{ mr: 1, minWidth: 120 }}>
                                            <InputLabel>Grade</InputLabel>
                                            <Select
                                                value={newGrades[student.id] || ''}
                                                onChange={(e) => handleGradeChange(student.id, e.target.value)}
                                                label="Grade"
                                            >
                                                <MenuItem value=""><em>Select grade</em></MenuItem>
                                                {[1, 2, 3, 4, 5, 6].map(g => <MenuItem key={g} value={g}>{g}</MenuItem>)}
                                            </Select>
                                        </FormControl>
                                        <Button
                                            variant="contained"
                                            onClick={() => handleAddGrade(student.id)}
                                            disabled={!newGrades[student.id]}
                                        >
                                            Add Grade
                                        </Button>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    );
};

export default ClassGrades;
