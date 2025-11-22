import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import keycloak from './keycloak';
import { List, ListItem, ListItemText, Paper, Typography } from '@mui/material';

const MyClasses = () => {
    const [subjects, setSubjects] = useState([]);

    useEffect(() => {
        const fetchSubjects = async () => {
            try {
                const response = await fetch('/api/subjects/my-subjects', {
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
        <Paper sx={{ p: 2 }}>
            <Typography variant="h4" sx={{ mb: 2 }}>My Classes</Typography>
            <List component="nav">
                {subjects.map(subject => (
                    <ListItem button component={Link} to={`/my-classes/${subject.id}`} key={subject.id}>
                        <ListItemText primary={subject.name} />
                    </ListItem>
                ))}
            </List>
        </Paper>
    );
};

export default MyClasses;
