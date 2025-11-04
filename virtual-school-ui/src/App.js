import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import './App.css';
import Students from './Students';
import Lecturers from './Lecturers';
import LessonPlans from './LessonPlans';
import Subjects from './Subjects';
import Groups from './Groups';
import MyTimetable from './MyTimetable';
import UserForm from './UserForm';
import TeacherTimetable from './TeacherTimetable';

function App({ keycloak }) {
  return (
    <Router>
      <div className="App">
        <nav className="navbar">
          <div className="navbar-container">
            <Link to="/" className="navbar-logo">
              Virtual School
            </Link>
            <ul className="nav-menu">
              {keycloak.hasRealmRole('admin') && (
                <>
                  <li className="nav-item">
                    <Link to="/students" className="nav-links">
                      Students
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to="/lecturers" className="nav-links">
                      Lecturers
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to="/subjects" className="nav-links">
                      Subjects
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to="/groups" className="nav-links">
                      Groups
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to="/lesson-plans" className="nav-links">
                      Lesson Plans
                    </Link>
                  </li>
                  <li className="nav-item">
                    <Link to="/create-user" className="nav-links">
                      Create User
                    </Link>
                  </li>
                </>
              )}
              {keycloak.hasRealmRole('student') && (
                <li className="nav-item">
                  <Link to="/my-timetable" className="nav-links">
                    My Timetable
                  </Link>
                </li>
              )}
              {keycloak.hasRealmRole('teacher') && (
                <li className="nav-item">
                  <Link to="/teacher-timetable" className="nav-links">
                    My Timetable
                  </Link>
                </li>
              )}
            </ul>
            <div className="nav-login">
              {keycloak.authenticated ? (
                <button className="btn" onClick={() => keycloak.logout()}>
                  Logout ({keycloak.tokenParsed.preferred_username})
                </button>
              ) : (
                <button className="btn" onClick={() => keycloak.login()}>
                  Login
                </button>
              )}
            </div>
          </div>
        </nav>

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
              <Route path="/" element={<h2>Welcome to the Virtual School Management System.</h2>} />
            </Routes>
          ) : (
            <div className="login-required">
              <h2>Please log in to access the content.</h2>
            </div>
          )}
        </main>
      </div>
    </Router>
  );
}

export default App;


