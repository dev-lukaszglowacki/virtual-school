import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

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
      fetchGroups(); // Refresh groups to show updated student list
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
      fetchGroups(); // Refresh groups
    } catch (err) {
      setError(err);
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Groups</h2>
      <button onClick={() => { setIsCreating(true); setEditingGroup(null); }}>Add Group</button>

      {(isCreating || editingGroup) && (
        <GroupForm group={editingGroup} onSave={handleSaveGroup} />
      )}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {groups.map((group) => (
            <tr key={group.id}>
              <td>{group.id}</td>
              <td>{group.name}</td>
              <td>
                <button onClick={() => { setEditingGroup(group); setIsCreating(false); }}>Edit</button>
                <button onClick={() => handleDeleteGroup(group.id)}>Delete</button>
                <button onClick={() => setSelectedGroup(group)}>Manage Students</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedGroup && (
        <ManageStudents
          group={selectedGroup}
          allStudents={students}
          onAssign={handleAssignStudent}
          onRemove={handleRemoveStudent}
          onClose={() => setSelectedGroup(null)}
        />
      )}
    </div>
  );
}

function GroupForm({ group, onSave }) {
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
    <form onSubmit={handleSubmit}>
      <input type="text" value={name} onChange={(e) => setName(e.target.value)} required />
      <button type="submit">Save</button>
    </form>
  );
}

function ManageStudents({ group, allStudents, onAssign, onRemove, onClose }) {
    const assignedStudentIds = new Set(group.students.map(s => s.id));
    const unassignedStudents = allStudents.filter(s => !assignedStudentIds.has(s.id));
  
    return (
      <div className="modal">
        <h3>Manage Students for {group.name}</h3>
        <h4>Assigned Students</h4>
        <ul>
          {group.students.map(student => (
            <li key={student.id}>
              {student.firstName} {student.lastName}
              <button onClick={() => onRemove(group.id, student.id)}>Remove</button>
            </li>
          ))}
        </ul>
        <h4>Unassigned Students</h4>
        <select onChange={(e) => onAssign(group.id, e.target.value)}>
          <option value="">Assign a student...</option>
          {unassignedStudents.map(student => (
            <option key={student.id} value={student.id}>
              {student.firstName} {student.lastName}
            </option>
          ))}
        </select>
        <button onClick={onClose}>Close</button>
      </div>
    );
  }

export default Groups;
