import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

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
        <div>
            <h2>My Grades</h2>
            <table>
                <thead>
                    <tr>
                        <th>Subject</th>
                        <th>Lecturer</th>
                        <th>Grade</th>
                    </tr>
                </thead>
                <tbody>
                    {grades.map(grade => (
                        <tr key={grade.id}>
                            <td>{grade.subjectName}</td>
                            <td>{grade.lecturerName}</td>
                            <td>{grade.grade}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default MyGrades;
