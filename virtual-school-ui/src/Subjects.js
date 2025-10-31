import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

function SubjectForm({ subject, onSave }) {
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
    <form onSubmit={handleSubmit}>
      <div>
        <label>Name</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Description</label>
        <input
          type="text"
          name="description"
          value={formData.description}
          onChange={handleChange}
        />
      </div>
      <button type="submit">Save</button>
    </form>
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

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Subjects</h2>
      <button onClick={() => { setIsCreating(true); setEditingSubject(null); }}>Add Subject</button>
      
      {(isCreating || editingSubject) && (
        <SubjectForm 
          subject={editingSubject} 
          onSave={handleSave} 
        />
      )}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Description</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {subjects.map((subject) => (
            <tr key={subject.id}>
              <td>{subject.id}</td>
              <td>{subject.name}</td>
              <td>{subject.description}</td>
              <td>
                <button onClick={() => { setEditingSubject(subject); setIsCreating(false); }}>Edit</button>
                <button onClick={() => handleDelete(subject.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Subjects;
