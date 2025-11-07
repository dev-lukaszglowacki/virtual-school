import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

function TeacherTimetable() {
  const [myLessonPlans, setMyLessonPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!keycloak.hasRealmRole('teacher')) {
        setLoading(false);
        return;
      }
      try {
        // Fetch current lecturer's data
        const meResponse = await fetch('/api/lecturers/me', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!meResponse.ok) throw new Error('Could not fetch lecturer data');
        const meData = await meResponse.json();

        // Fetch all lesson plans and filter them
        const plansResponse = await fetch('/api/lesson-plans', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!plansResponse.ok) throw new Error('Could not fetch lesson plans');
        const allPlans = await plansResponse.json();
        
        const filteredPlans = allPlans.filter(plan => plan.lecturerName === `${meData.firstName} ${meData.lastName}`);
        setMyLessonPlans(filteredPlans);

      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
  const timetable = days.map(day => ({
    day,
    lessons: myLessonPlans
      .filter(plan => plan.dayOfWeek === day)
      .sort((a, b) => a.startTime.localeCompare(b.startTime)),
  }));

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  return (
    <div>
      <h2>My Timetable</h2>
      <div style={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-around' }}>
        {timetable.map(({ day, lessons }) => (
          <div key={day} style={{ width: '18%' }}>
            <h3>{day}</h3>
            {lessons.length > 0 ? (
              lessons.map(plan => (
                <div key={plan.id} style={{ border: '1px solid #ccc', margin: '5px', padding: '10px' }}>
                  <strong>{plan.subjectName}</strong><br />
                  <em>Group: {plan.studentGroupName}</em><br />
                  <span>{plan.startTime} - {plan.endTime}</span>
                </div>
              ))
            ) : (
              <p>No lessons scheduled.</p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default TeacherTimetable;
