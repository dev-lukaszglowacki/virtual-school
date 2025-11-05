import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import keycloak from './keycloak';

const MyClasses = () => {
    const [subjects, setSubjects] = useState([]);

    useEffect(() => {
        const fetchSubjects = async () => {
            try {
                const response = await fetch('http://localhost:8080/api/subjects/my-subjects', {
                    headers: {
                        'Authorization': `Bearer ${keycloak.token}`
                    }
                });
                if (response.ok) {
                    const data = await response.json();
                    setSubjects(data);
                } else {
                    console.error('Failed to fetch subjects');
                }
            } catch (error) {
                console.error('Error fetching subjects:', error);
            }
        };

        fetchSubjects();
    }, []);

    return (
        <div>
            <h2>My Classes</h2>
            <ul>
                {subjects.map(subject => (
                    <li key={subject.id}>
                        <Link to={`/my-classes/${subject.id}`}>{subject.name}</Link>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MyClasses;
