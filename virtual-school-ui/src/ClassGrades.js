import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import keycloak from './keycloak';

const ClassGrades = () => {
    const { subjectId } = useParams();
    const [students, setStudents] = useState([]);
    const [grades, setGrades] = useState({});
    const [newGrades, setNewGrades] = useState({});

    useEffect(() => {
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
                console.error('Error fetching students:', error);
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
        <div>
            <h2>Class Grades</h2>
            <table>
                <thead>
                    <tr>
                        <th>Student</th>
                        <th>Grades</th>
                        <th>New Grade</th>
                    </tr>
                </thead>
                <tbody>
                    {students.map(student => (
                        <tr key={student.id}>
                            <td>{student.firstName} {student.lastName}</td>
                            <td>
                                {grades[student.id] && grades[student.id].map(grade => grade.grade).join(', ')}
                            </td>
                            <td>
                                <input
                                    type="number"
                                    value={newGrades[student.id] || ''}
                                    onChange={(e) => handleGradeChange(student.id, e.target.value)}
                                />
                                <button onClick={() => handleAddGrade(student.id)}>Add Grade</button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default ClassGrades;
