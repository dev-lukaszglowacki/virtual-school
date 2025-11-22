import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, ThemeProvider, CssBaseline } from '@mui/material';
import './App.css';
import Students from './Students';
import Lecturers from './Lecturers';
import LessonPlans from './LessonPlans';
import Subjects from './Subjects';
import Groups from './Groups';
import MyTimetable from './MyTimetable';
import UserForm from './UserForm';
import TeacherTimetable from './TeacherTimetable';
import MyGrades from './MyGrades';
import MyClasses from './MyClasses';
import ClassGrades from './ClassGrades';
import Layout from './Layout';
import theme from './theme';

function App({ keycloak }) {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router>
        <Layout keycloak={keycloak}>
          <Container sx={{ mt: 4 }}>
            <main className="main-content">
              {keycloak.authenticated ? (
                <Routes>
                  <Route path="/students" element={<Students />} />
                  <Route path="/lecturers" element={<Lecturers />} />
                  <Route path="/subjects" element={<Subjects />} />
                  <Route path="/groups" element={<Groups />} />
                  <Route path="/lesson-plans" element={<LessonPlans />} />
                  <Route path="/my-timetable" element={<MyTimetable />} />
                  <Route path="/create-user" element={<UserForm />} />
                  <Route path="/teacher-timetable" element={<TeacherTimetable />} />
                  <Route path="/my-grades" element={<MyGrades />} />
                  <Route path="/my-classes" element={<MyClasses />} />
                  <Route path="/my-classes/:subjectId" element={<ClassGrades />} />
                  <Route path="/" element={<h2>Welcome to the Virtual School Management System.</h2>} />
                </Routes>
              ) : (
                <div className="login-required">
                  <h2>Please log in to access the content.</h2>
                </div>
              )}
            </main>
          </Container>
        </Layout>
      </Router>
    </ThemeProvider>
  );
}

export default App;
