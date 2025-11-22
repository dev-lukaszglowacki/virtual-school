import React from 'react';
import NavigationBar from './NavigationBar';

const Layout = ({ children, keycloak }) => {
    return (
        <div>
            <NavigationBar keycloak={keycloak} />
            <main>{children}</main>
        </div>
    );
};

export default Layout;
