import React from 'react';
import { AppBar, Toolbar, Typography, Button } from '@mui/material';
import { Link } from 'react-router-dom';

const NavigationBar = ({ keycloak }) => {
    return (
        <AppBar position="static">
            <Toolbar>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    <Button color="inherit" component={Link} to="/" sx={{ textTransform: 'none', fontSize: '1.25rem' }}>
                        Virtual School
                    </Button>
                </Typography>
                {keycloak && keycloak.authenticated && (
                    <>
                        {keycloak.hasRealmRole('admin') && (
                            <>
                                <Button color="inherit" component={Link} to="/students">Students</Button>
                                <Button color="inherit" component={Link} to="/lecturers">Lecturers</Button>
                                <Button color="inherit" component={Link} to="/subjects">Subjects</Button>
                                <Button color="inherit" component={Link} to="/groups">Groups</Button>
                                <Button color="inherit" component={Link} to="/lesson-plans">Lesson Plans</Button>
                                <Button color="inherit" component={Link} to="/create-user">Create User</Button>
                            </>
                        )}
                        {keycloak.hasRealmRole('student') && (
                            <>
                                <Button color="inherit" component={Link} to="/my-timetable">My Timetable</Button>
                                <Button color="inherit" component={Link} to="/my-grades">My Grades</Button>
                            </>
                        )}
                        {keycloak.hasRealmRole('teacher') && (
                            <>
                                <Button color="inherit" component={Link} to="/teacher-timetable">My Timetable</Button>
                                <Button color="inherit" component={Link} to="/my-classes">My Classes</Button>
                            </>
                        )}
                    </>
                )}
                <div style={{ marginLeft: 'auto' }}>
                    {keycloak && keycloak.authenticated ? (
                        <Button color="inherit" onClick={() => keycloak.logout()}>
                            Logout ({keycloak.tokenParsed.preferred_username})
                        </Button>
                    ) : (
                        <Button color="inherit" onClick={() => keycloak.login()}>
                            Login
                        </Button>
                    )}
                </div>
            </Toolbar>
        </AppBar>
    );
};

export default NavigationBar;
