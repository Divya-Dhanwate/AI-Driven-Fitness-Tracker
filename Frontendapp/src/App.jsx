import { useContext, useEffect, useState } from 'react'
import { Navigate, Route, Routes, BrowserRouter as Router } from 'react-router-dom' 
import { Button, Box } from '@mui/material' 

import './App.css'
import { AuthContext } from 'react-oauth2-code-pkce'
import { useDispatch } from 'react-redux'
import { setCredentials, logout } from './Store/authslice'
import { addedActivity } from './services/api'
import ActivityForm from './components/ActivityForm'
import ActivityList from './components/ActivityList'
import ActivityDetails from './components/ActivityDetails'

const ActivityPage = () => {

  const handleAddedActivity = async (activityData) => {
    await addedActivity(activityData);
  };

  return (
    <>
      <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}></Box>
      <ActivityForm 
        addedActivity={handleAddedActivity} 
        onActivitysAdded={() => window.location.reload()} 
      />
      <ActivityList />
    </>
  );
};


function App() {
  const { token, tokenData, logIn, logOut } = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  useEffect(() => {
    if (token) {
      dispatch(setCredentials({ token, user: tokenData }));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);

  const handleLogout = () => {
    dispatch(logout());
    logOut();
  };

  return (
    <Router>
      {!token ? (
        <Button 
          variant="contained" 
          sx={{ backgroundColor: '#dc0048' }} 
          onClick={() => logIn()}
        >
          Login
        </Button>
      ) : (
        <>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', p: 2 }}>
            <Button variant="outlined" color="secondary" onClick={handleLogout}>
              Logout
            </Button>
          </Box>
          <Routes>
            <Route path="/activities" element={<ActivityPage />} />
            <Route path="/activities/:id" element={<ActivityDetails />} />
            <Route path="/" element={<Navigate to="/activities" replace />} />
          </Routes>
        </>
      )}
    </Router>
  )
}

export default App
