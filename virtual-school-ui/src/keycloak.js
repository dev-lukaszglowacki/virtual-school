import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8082',
  realm: 'virtual-school',
  clientId: 'virtual-school-ui',
});

export default keycloak;
