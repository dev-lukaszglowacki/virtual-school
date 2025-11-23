import React, { useState, useEffect } from 'react';
import keycloak from './keycloak';
import { Grid, Card, CardContent, Typography, Paper, CircularProgress, Box } from '@mui/material';

function MyTimetable() {
  const [myGroups, setMyGroups] = useState([]);
  const [allLessonPlans, setAllLessonPlans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const groupsResponse = await fetch('/api/users/me/groups', {
          headers: { Authorization: `Bearer ${keycloak.token}` },
        });
        if (!groupsResponse.ok) throw new Error('Could not fetch student groups');
        const groupsData = await groupsResponse.json();
        setMyGroups(groupsData);

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

    if (keycloak.hasRealmRole('student')) {
      fetchData();
    } else {
      setLoading(false);
    }
  }, []);

  const myGroupNames = new Set(myGroups.map(g => g.name));
  const filteredPlans = allLessonPlans.filter(plan => myGroupNames.has(plan.studentGroupName));

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];
  const timetable = days.map(day => ({
    day,
    lessons: filteredPlans
      .filter(plan => plan.dayOfWeek === day)
      .sort((a, b) => a.startTime.localeCompare(b.startTime)),
  }));

  if (loading) return <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}><CircularProgress /></Box>;
  if (error) return <Typography color="error">Error: {error.message}</Typography>;

  return (
    <Paper sx={{ p: 2 }}>
      <Typography variant="h4" sx={{ mb: 2 }}>My Timetable</Typography>
      <Grid container spacing={2}>
        {timetable.map(({ day, lessons }) => (
          <Grid item xs={12} sm={6} md={2.4} key={day}>
            <Paper elevation={2} sx={{ p: 2, height: '100%' }}>
              <Typography variant="h6" align="center">{day}</Typography>
              {lessons.length > 0 ? (
                lessons.map(plan => (
                  <Card key={plan.id} sx={{ mb: 1 }}>
                    <CardContent>
                      <Typography variant="body1" component="div">
                        <strong>{plan.subjectName}</strong>
                      </Typography>
                      <Typography sx={{ mb: 1.5 }} color="text.secondary">
                        {plan.lecturerName}
                      </Typography>
                      <Typography variant="body2">
                        {plan.startTime} - {plan.endTime}
                      </Typography>
                    </CardContent>
                  </Card>
                ))
              ) : (
                <Typography sx={{ mt: 2 }} align="center">No lessons scheduled.</Typography>
              )}
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Paper>
  );
}

export default MyTimetable;
