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
    Button,
    Typography,
    Box,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    TextField,
    IconButton
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';

function SubjectForm({ subject, onSave, onCancel }) {
    const [formData, setFormData] = useState({
        name: '',
        description: '',
    });

    useEffect(() => {
        if (subject) {
            setFormData(subject);
        } else {
            setFormData({ name: '', description: '' });
        }
    }, [subject]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData);
    };

    return (
        <Dialog open onClose={onCancel}>
            <DialogTitle>{subject ? 'Edit Subject' : 'Add Subject'}</DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        name="name"
                        label="Name"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                    <TextField
                        margin="dense"
                        name="description"
                        label="Description"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={formData.description}
                        onChange={handleChange}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={onCancel}>Cancel</Button>
                    <Button type="submit">Save</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}

function Subjects() {
    const [subjects, setSubjects] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingSubject, setEditingSubject] = useState(null);
    const [isCreating, setIsCreating] = useState(false);

    const fetchSubjects = async () => {
        try {
            setLoading(true);
            const response = await fetch('/api/subjects', {
                headers: {
                    Authorization: `Bearer ${keycloak.token}`,
                },
            });
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setSubjects(data);
        } catch (error) {
            setError(error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSubjects();
    }, []);

    const handleSave = async (subject) => {
        const url = subject.id ? `/api/subjects/${subject.id}` : '/api/subjects';
        const method = subject.id ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${keycloak.token}`,
                },
                body: JSON.stringify(subject),
            });
            if (!response.ok) {
                throw new Error('Failed to save subject');
            }
            setEditingSubject(null);
            setIsCreating(false);
            fetchSubjects();
        } catch (error) {
            setError(error);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this subject?')) {
            try {
                const response = await fetch(`/api/subjects/${id}`, {
                    method: 'DELETE',
                    headers: {
                        Authorization: `Bearer ${keycloak.token}`,
                    },
                });
                if (!response.ok) {
                    throw new Error('Failed to delete subject');
                }
                fetchSubjects();
            } catch (error) {
                setError(error);
            }
        }
    };

    const handleCloseForm = () => {
        setIsCreating(false);
        setEditingSubject(null);
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;

    return (
        <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Subjects</Typography>
                {keycloak.hasRealmRole('admin') && (
                    <Button variant="contained" onClick={() => { setIsCreating(true); setEditingSubject(null); }}>Add Subject</Button>
                )}
            </Box>

            {(isCreating || editingSubject) && (
                <SubjectForm
                    subject={editingSubject}
                    onSave={handleSave}
                    onCancel={handleCloseForm}
                />
            )}

            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Name</TableCell>
                            <TableCell>Description</TableCell>
                            {keycloak.hasRealmRole('admin') && <TableCell>Actions</TableCell>}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {subjects.map((subject) => (
                            <TableRow key={subject.id}>
                                <TableCell>{subject.id}</TableCell>
                                <TableCell>{subject.name}</TableCell>
                                <TableCell>{subject.description}</TableCell>
                                {keycloak.hasRealmRole('admin') && (
                                    <TableCell>
                                        <IconButton onClick={() => { setEditingSubject(subject); setIsCreating(false); }}><Edit /></IconButton>
                                        <IconButton onClick={() => handleDelete(subject.id)}><Delete /></IconButton>
                                    </TableCell>
                                )}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    );
}

export default Subjects;
