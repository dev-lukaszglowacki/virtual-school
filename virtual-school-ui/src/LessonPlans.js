import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

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

    // Format data for the backend
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

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Lesson Plans</h2>
      <button onClick={() => { setIsCreating(true); setEditingPlan(null); }}>Add Lesson Plan</button>

      {(isCreating || editingPlan) && (
        <LessonPlanForm
          plan={editingPlan}
          subjects={subjects}
          lecturers={lecturers}
          groups={groups}
          onSave={handleSave}
        />
      )}

      <table>
        <thead>
          <tr>
            <th>Subject</th>
            <th>Lecturer</th>
            <th>Group</th>
            <th>Day</th>
            <th>Time</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {lessonPlans.map((plan) => (
            <tr key={plan.id}>
              <td>{plan.subjectName}</td>
              <td>{plan.lecturerName}</td>
              <td>{plan.studentGroupName}</td>
              <td>{plan.dayOfWeek}</td>
              <td>{plan.startTime} - {plan.endTime}</td>
              <td>
                <button onClick={() => { setEditingPlan(plan); setIsCreating(false); }}>Edit</button>
                <button onClick={() => handleDelete(plan.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function LessonPlanForm({ plan, subjects, lecturers, groups, onSave }) {
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
    <form onSubmit={handleSubmit}>
      <select name="subjectId" value={formData.subjectId} onChange={handleChange} required>
        <option value="">Select Subject</option>
        {subjects.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
      </select>
      <select name="lecturerId" value={formData.lecturerId} onChange={handleChange} required>
        <option value="">Select Lecturer</option>
        {lecturers.map(l => <option key={l.id} value={l.id}>{l.firstName} {l.lastName}</option>)}
      </select>
      <select name="groupId" value={formData.groupId} onChange={handleChange} required>
        <option value="">Select Group</option>
        {groups.map(g => <option key={g.id} value={g.id}>{g.name}</option>)}
      </select>
      <select name="dayOfWeek" value={formData.dayOfWeek} onChange={handleChange} required>
        {days.map(day => <option key={day} value={day}>{day}</option>)}
      </select>
      <input type="time" name="startTime" value={formData.startTime} onChange={handleChange} required />
      <input type="time" name="endTime" value={formData.endTime} onChange={handleChange} required />
      <button type="submit">Save</button>
    </form>
  );
}

export default LessonPlans;