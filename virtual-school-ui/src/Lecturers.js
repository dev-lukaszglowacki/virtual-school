import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';
import LecturerForm from './LecturerForm';

function Lecturers() {
  const [lecturers, setLecturers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingLecturer, setEditingLecturer] = useState(null);
  const [isCreating, setIsCreating] = useState(false);

  const fetchLecturers = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/lecturers', {
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setLecturers(data);
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLecturers();
  }, []);

  const handleSave = async (lecturer) => {
    const url = lecturer.id ? `/api/lecturers/${lecturer.id}` : '/api/lecturers';
    const method = lecturer.id ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify(lecturer),
      });
      if (!response.ok) {
        throw new Error('Failed to save lecturer');
      }
      setEditingLecturer(null);
      setIsCreating(false);
      fetchLecturers();
    } catch (error) {
      setError(error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this lecturer?')) {
      try {
        const response = await fetch(`/api/lecturers/${id}`, {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
          },
        });
        if (!response.ok) {
          throw new Error('Failed to delete lecturer');
        }
        fetchLecturers();
      } catch (error) {
        setError(error);
      }
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Lecturers</h2>
      <button onClick={() => { setIsCreating(true); setEditingLecturer(null); }}>Add Lecturer</button>
      
      {(isCreating || editingLecturer) && (
        <LecturerForm 
          lecturer={editingLecturer} 
          onSave={handleSave} 
        />
      )}

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Subject</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {lecturers.map((lecturer) => (
            <tr key={lecturer.id}>
              <td>{lecturer.id}</td>
              <td>{lecturer.firstName}</td>
              <td>{lecturer.lastName}</td>
              <td>{lecturer.email}</td>
              <td>{lecturer.subject}</td>
              <td>
                <button onClick={() => { setEditingLecturer(lecturer); setIsCreating(false); }}>Edit</button>
                <button onClick={() => handleDelete(lecturer.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Lecturers;