import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import keycloak from './keycloak';

keycloak.init({ onLoad: 'login-required', checkLoginIframe: false }).then(authenticated => {
  if (authenticated) {
    const root = ReactDOM.createRoot(document.getElementById('root'));
    root.render(
      <React.StrictMode>
        <App keycloak={keycloak} />
      </React.StrictMode>
    );
  } else {
    console.warn('Not authenticated');
  }
}).catch(error => {
  console.error('Keycloak initialization failed', error);
});

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
