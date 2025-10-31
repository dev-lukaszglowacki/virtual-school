import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';
import StudentForm from './StudentForm';

function Students() {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingStudent, setEditingStudent] = useState(null);
  const [isCreating, setIsCreating] = useState(false);

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const response = await fetch('/api/students', {
        headers: {
          Authorization: `Bearer ${keycloak.token}`,
        },
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setStudents(data);
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, []);

  const handleSave = async (student) => {
    const url = student.id ? `/api/students/${student.id}` : '/api/students';
    const method = student.id ? 'PUT' : 'POST';

    try {
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${keycloak.token}`,
        },
        body: JSON.stringify(student),
      });
      if (!response.ok) {
        throw new Error('Failed to save student');
      }
      setEditingStudent(null);
      setIsCreating(false);
      fetchStudents();
    } catch (error) {
      setError(error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this student?')) {
      try {
        const response = await fetch(`/api/students/${id}`, {
          method: 'DELETE',
          headers: {
            Authorization: `Bearer ${keycloak.token}`,
          },
        });
        if (!response.ok) {
          throw new Error('Failed to delete student');
        }
        fetchStudents();
      } catch (error) {
        setError(error);
      }
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Students</h2>
      <button onClick={() => { setIsCreating(true); setEditingStudent(null); }}>Add Student</button>
      
      {(isCreating || editingStudent) && (
        <StudentForm 
          student={editingStudent} 
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
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {students.map((student) => (
            <tr key={student.id}>
              <td>{student.id}</td>
              <td>{student.firstName}</td>
              <td>{student.lastName}</td>
              <td>{student.email}</td>
              <td>
                <button onClick={() => { setEditingStudent(student); setIsCreating(false); }}>Edit</button>
                <button onClick={() => handleDelete(student.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Students;