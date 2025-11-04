import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import keycloak from './keycloak';

function Lecturers() {
  const [lecturers, setLecturers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

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

  const handleAddLecturer = () => {
    navigate('/create-user', { state: { role: 'teacher' } });
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>Lecturers</h2>
      {keycloak.hasRealmRole('admin') && (
        <button onClick={handleAddLecturer}>Add Lecturer</button>
      )}
      
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          {lecturers.map((lecturer) => (
            <tr key={lecturer.id}>
              <td>{lecturer.id}</td>
              <td>{lecturer.firstName}</td>
              <td>{lecturer.lastName}</td>
              <td>{lecturer.email}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Lecturers;