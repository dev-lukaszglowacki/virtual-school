import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';

function MyTimetable() {
  const [myGroups, setMyGroups] = useState([]);
  const [allLessonPlans, setAllLessonPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch current student's data
        const meResponse = await fetch('/api/students/me', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!meResponse.ok) throw new Error('Could not fetch student data');
        const meData = await meResponse.json();

        // Fetch all groups and filter them to find the student's groups
        const groupsResponse = await fetch('/api/groups', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!groupsResponse.ok) throw new Error('Could not fetch groups');
        const groupsData = await groupsResponse.json();
        console.log('groupsData:', groupsData);
        const studentGroups = groupsData.filter(group => group.students.some(student => student.id === meData.id));
        setMyGroups(studentGroups);

        // Fetch all lesson plans
        const plansResponse = await fetch('/api/lesson-plans', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!plansResponse.ok) throw new Error('Could not fetch lesson plans');
        const plansData = await plansResponse.json();
        setAllLessonPlans(plansData);

      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const myGroupIds = new Set(myGroups.map(g => g.id));
  const filteredPlans = allLessonPlans.filter(plan => myGroupIds.has(plan.studentGroup?.id));

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
  const timetable = days.map(day => ({
    day,
    lessons: filteredPlans
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
                  <strong>{plan.subject?.name}</strong><br />
                  <em>{plan.lecturer?.firstName} {plan.lecturer?.lastName}</em><br />
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

export default MyTimetable;
