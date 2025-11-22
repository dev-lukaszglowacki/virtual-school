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
    Select,
    MenuItem,
    FormControl,
    InputLabel
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';

function LessonPlans() {
    const [lessonPlans, setLessonPlans] = useState([]);
    const [subjects, setSubjects] = useState([]);
    const [lecturers, setLecturers] = useState([]);
    const [groups, setGroups] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [editingPlan, setEditingPlan] = useState(null);
    const [isCreating, setIsCreating] = useState(false);

    const fetchData = async () => {
        try {
            const [plansRes, subjectsRes, lecturersRes, groupsRes] = await Promise.all([
                fetch('/api/lesson-plans', { headers: { Authorization: `Bearer ${keycloak.token}` } }),
                fetch('/api/subjects', { headers: { Authorization: `Bearer ${keycloak.token}` } }),
                fetch('/api/lecturers', { headers: { Authorization: `Bearer ${keycloak.token}` } }),
                fetch('/api/groups', { headers: { Authorization: `Bearer ${keycloak.token}` } }),
            ]);
            if (!plansRes.ok || !subjectsRes.ok || !lecturersRes.ok || !groupsRes.ok) {
                throw new Error('Failed to fetch data');
            }
            const plans = await plansRes.json();
            const subjects = await subjectsRes.json();
            const lecturers = await lecturersRes.json();
            const groups = await groupsRes.json();
            setLessonPlans(plans);
            setSubjects(subjects);
            setLecturers(lecturers);
            setGroups(groups);
        } catch (err) {
            setError(err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleSave = async (plan) => {
        const url = plan.id ? `/api/lesson-plans/${plan.id}` : '/api/lesson-plans';
        const method = plan.id ? 'PUT' : 'POST';

        const payload = {
            ...plan,
            subject: { id: plan.subjectId },
            lecturer: { id: plan.lecturerId },
            studentGroup: { id: plan.groupId },
        };

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${keycloak.token}` },
                body: JSON.stringify(payload),
            });
            if (!response.ok) {
                throw new Error('Failed to save lesson plan');
            }
            setEditingPlan(null);
            setIsCreating(false);
            fetchData();
        } catch (err) {
            setError(err);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure?')) {
            try {
                const response = await fetch(`/api/lesson-plans/${id}`, {
                    method: 'DELETE',
                    headers: { Authorization: `Bearer ${keycloak.token}` },
                });
                if (!response.ok) {
                    throw new Error('Failed to delete lesson plan');
                }
                fetchData();
            } catch (err) {
                setError(err);
            }
        }
    };
    
    const handleCloseForm = () => {
        setIsCreating(false);
        setEditingPlan(null);
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;

    return (
        <Paper sx={{ p: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                <Typography variant="h4">Lesson Plans</Typography>
                {keycloak.hasRealmRole('admin') && (
                    <Button variant="contained" onClick={() => { setIsCreating(true); setEditingPlan(null); }}>Add Lesson Plan</Button>
                )}
            </Box>

            {(isCreating || editingPlan) && (
                <LessonPlanForm
                    plan={editingPlan}
                    subjects={subjects}
                    lecturers={lecturers}
                    groups={groups}
                    onSave={handleSave}
                    onCancel={handleCloseForm}
                />
            )}

            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Subject</TableCell>
                            <TableCell>Lecturer</TableCell>
                            <TableCell>Group</TableCell>
                            <TableCell>Day</TableCell>
                            <TableCell>Time</TableCell>
                            {keycloak.hasRealmRole('admin') && <TableCell>Actions</TableCell>}
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {lessonPlans.map((plan) => (
                            <TableRow key={plan.id}>
                                <TableCell>{plan.subjectName}</TableCell>
                                <TableCell>{plan.lecturerName}</TableCell>
                                <TableCell>{plan.studentGroupName}</TableCell>
                                <TableCell>{plan.dayOfWeek}</TableCell>
                                <TableCell>{plan.startTime} - {plan.endTime}</TableCell>
                                {keycloak.hasRealmRole('admin') && (
                                    <TableCell>
                                        <IconButton onClick={() => { setEditingPlan(plan); setIsCreating(false); }}><Edit /></IconButton>
                                        <IconButton onClick={() => handleDelete(plan.id)}><Delete /></IconButton>
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

function LessonPlanForm({ plan, subjects, lecturers, groups, onSave, onCancel }) {
    const [formData, setFormData] = useState({
        subjectId: '',
        lecturerId: '',
        groupId: '',
        dayOfWeek: 'MONDAY',
        startTime: '',
        endTime: '',
    });

    useEffect(() => {
        if (plan) {
            const lecturer = lecturers.find(l => `${l.firstName} ${l.lastName}` === plan.lecturerName);
            setFormData({
                id: plan.id,
                subjectId: subjects.find(s => s.name === plan.subjectName)?.id || '',
                lecturerId: lecturer?.id || '',
                groupId: groups.find(g => g.name === plan.studentGroupName)?.id || '',
                dayOfWeek: plan.dayOfWeek || 'MONDAY',
                startTime: plan.startTime || '',
                endTime: plan.endTime || '',
            });
        }
    }, [plan, subjects, lecturers, groups]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSave(formData);
    };

    const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

    return (
        <Dialog open onClose={onCancel}>
            <DialogTitle>{plan ? 'Edit Lesson Plan' : 'Add Lesson Plan'}</DialogTitle>
            <form onSubmit={handleSubmit}>
                <DialogContent>
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Subject</InputLabel>
                        <Select name="subjectId" value={formData.subjectId} onChange={handleChange} required>
                            {subjects.map(s => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Lecturer</InputLabel>
                        <Select name="lecturerId" value={formData.lecturerId} onChange={handleChange} required>
                            {lecturers.map(l => <MenuItem key={l.id} value={l.id}>{l.firstName} {l.lastName}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Group</InputLabel>
                        <Select name="groupId" value={formData.groupId} onChange={handleChange} required>
                            {groups.map(g => <MenuItem key={g.id} value={g.id}>{g.name}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Day of Week</InputLabel>
                        <Select name="dayOfWeek" value={formData.dayOfWeek} onChange={handleChange} required>
                            {days.map(day => <MenuItem key={day} value={day}>{day}</MenuItem>)}
                        </Select>
                    </FormControl>
                    <TextField
                        margin="dense"
                        name="startTime"
                        label="Start Time"
                        type="time"
                        fullWidth
                        value={formData.startTime}
                        onChange={handleChange}
                        required
                        InputLabelProps={{ shrink: true }}
                    />
                    <TextField
                        margin="dense"
                        name="endTime"
                        label="End Time"
                        type="time"
                        fullWidth
                        value={formData.endTime}
                        onChange={handleChange}
                        required
                        InputLabelProps={{ shrink: true }}
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

export default LessonPlans;