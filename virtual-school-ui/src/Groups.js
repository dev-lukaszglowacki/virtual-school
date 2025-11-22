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
    IconButton,
    List,
    ListItem,
    ListItemText,
    Select,
    MenuItem,
    FormControl,
    InputLabel
} from '@mui/material';
import { Edit, Delete, PersonAdd } from '@mui/icons-material';

function Groups() {
    const [groups, setGroups] = useState([]);
    const [students, setStudents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedGroup, setSelectedGroup] = useState(null);
    const [editingGroup, setEditingGroup] = useState(null);
    const [isCreating, setIsCreating] = useState(false);

    const fetchGroups = async () => {
        try {
            const response = await fetch('/api/groups', { headers: { Authorization: `Bearer ${keycloak.token}` } });
            if (!response.ok) throw new Error('Failed to fetch groups');
            const data = await response.json();
            setGroups(data);
        } catch (err) {
            setError(err);
        }
    };

    const fetchStudents = async () => {
        try {
            const response = await fetch('/api/students', { headers: { Authorization: `Bearer ${keycloak.token}` } });
            if (!response.ok) throw new Error('Failed to fetch students');
            const data = await response.json();
            setStudents(data);
        } catch (err) {
            setError(err);
        }
    };

    useEffect(() => {
        Promise.all([fetchGroups(), fetchStudents()]).finally(() => setLoading(false));
    }, []);

    const handleSaveGroup = async (group) => {
        const url = group.id ? `/api/groups/${group.id}` : '/api/groups';
        const method = group.id ? 'PUT' : 'POST';
        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${keycloak.token}` },
                body: JSON.stringify({ name: group.name }),
            });
            if (!response.ok) throw new Error('Failed to save group');
            setEditingGroup(null);
            setIsCreating(false);
            fetchGroups();
        } catch (err) {
            setError(err);
        }
    };

    const handleDeleteGroup = async (id) => {
        if (window.confirm('Are you sure?')) {
            try {
                const response = await fetch(`/api/groups/${id}`, {
                    method: 'DELETE',
                    headers: { Authorization: `Bearer ${keycloak.token}` },
                });
                if (!response.ok) throw new Error('Failed to delete group');
                fetchGroups();
            } catch (err) {
                setError(err);
            }
        }
    };

    const handleAssignStudent = async (groupId, studentId) => {
        try {
            const response = await fetch(`/api/groups/${groupId}/students/${studentId}`, {
                method: 'POST',
                headers: { Authorization: `Bearer ${keycloak.token}` },
            });
            if (!response.ok) throw new Error('Failed to assign student');
            fetchGroups(); 
        } catch (err) {
            setError(err);
        }
    };

    const handleRemoveStudent = async (groupId, studentId) => {
        try {
            const response = await fetch(`/api/groups/${groupId}/students/${studentId}`, {
                method: 'DELETE',
                headers: { Authorization: `Bearer ${keycloak.token}` },
            });
            if (!response.ok) throw new Error('Failed to remove student');
            fetchGroups(); 
        } catch (err) {
            setError(err);
        }
    };

    const handleCloseForms = () => {
        setIsCreating(false);
        setEditingGroup(null);
        setSelectedGroup(null);
    }

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;

    return (
        <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Groups</Typography>
                {keycloak.hasRealmRole('admin') && (
                    <Button variant="contained" onClick={() => { setIsCreating(true); setEditingGroup(null); }}>Add Group</Button>
                )}
            </Box>

            {(isCreating || editingGroup) && (
                <GroupForm group={editingGroup} onSave={handleSaveGroup} onCancel={handleCloseForms} />
            )}

            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>ID</TableCell>
                            <TableCell>Name</TableCell>
                            {keycloak.hasRealmRole('admin') && <TableCell>Actions</TableCell>}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {groups.map((group) => (
                            <TableRow key={group.id}>
                                <TableCell>{group.id}</TableCell>
                                <TableCell>{group.name}</TableCell>
                                {keycloak.hasRealmRole('admin') && (
                                    <TableCell>
                                        <IconButton onClick={() => { setEditingGroup(group); setIsCreating(false); }}><Edit /></IconButton>
                                        <IconButton onClick={() => handleDeleteGroup(group.id)}><Delete /></IconButton>
                                        <IconButton onClick={() => setSelectedGroup(group)}><PersonAdd /></IconButton>
                                    </TableCell>
                                )}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {selectedGroup && (
                <ManageStudents
                    group={selectedGroup}
                    allStudents={students}
                    onAssign={handleAssignStudent}
                    onRemove={handleRemoveStudent}
                    onClose={handleCloseForms}
                />
            )}
        </Paper>
    );
}

function GroupForm({ group, onSave, onCancel }) {
    const [name, setName] = useState('');
    useEffect(() => {
        if (group) setName(group.name);
        else setName('');
    }, [group]);

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave({ ...group, name });
    };

    return (
        <Dialog open onClose={onCancel}>
            <DialogTitle>{group ? 'Edit Group' : 'Add Group'}</DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Group Name"
                        type="text"
                        fullWidth
                        variant="standard"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
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

function ManageStudents({ group, allStudents, onAssign, onRemove, onClose }) {
    const assignedStudentIds = new Set(group.students.map(s => s.id));
    const unassignedStudents = allStudents.filter(s => !assignedStudentIds.has(s.id));

    return (
        <Dialog open onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle>Manage Students for {group.name}</DialogTitle>
            <DialogContent>
                <Typography variant="h6">Assigned Students</Typography>
                <List>
                    {group.students.map(student => (
                        <ListItem key={student.id} secondaryAction={
                            <IconButton edge="end" aria-label="delete" onClick={() => onRemove(group.id, student.id)}>
                                <Delete />
                            </IconButton>
                        }>
                            <ListItemText primary={`${student.firstName} ${student.lastName}`} />
                        </ListItem>
                    ))}
                </List>
                <Typography variant="h6" sx={{ mt: 2 }}>Unassigned Students</Typography>
                <FormControl fullWidth sx={{ mt: 1 }}>
                    <InputLabel id="assign-student-label">Assign a student...</InputLabel>
                    <Select
                        labelId="assign-student-label"
                        onChange={(e) => onAssign(group.id, e.target.value)}
                        value=""
                    >
                        {unassignedStudents.map(student => (
                            <MenuItem key={student.id} value={student.id}>
                                {student.firstName} {student.lastName}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose}>Close</Button>
            </DialogActions>
        </Dialog>
    );
}

export default Groups;
