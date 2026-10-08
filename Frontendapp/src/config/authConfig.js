import { AuthProvider } from 'react-oauth2-code-pkce'

export const authConfig = {
  // 1. Core Endpoints & Identity
  clientId: 'oauth2-pkce-client',
  authorizationEndpoint: 'http://localhost:8084/realms/fitness-oauth2/protocol/openid-connect/auth',
  tokenEndpoint: 'http://localhost:8084/realms/fitness-oauth2/protocol/openid-connect/token',
    redirectUri: 'http://localhost:5173', 
  scope: 'openid profile email', 
  
  
  autoLogin: true,             
  decodeToken: true,          
  

  onRefreshTokenExpire: (event) => event.login()

}
