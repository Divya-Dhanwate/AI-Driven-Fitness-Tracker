import React, { useEffect, useState } from 'react';
import { Button, Box, Grid, Card, CardContent, Typography } from '@mui/material';
import { useSelector } from 'react-redux';
import { getActivities, deleteActivity } from '../services/api';
import { useNavigate } from "react-router-dom";

export default function ActivityList() {
  const [activities, setActivities] = useState([]);
  const currentUserId = useSelector((state) => state.auth.userId);
  const navigate = useNavigate();

  const fetchActivities = async () => {
    try {
      const response = await getActivities();
      setActivities(response.data);
    } catch (error) {
      console.error('Error fetching activities:', error);
    }
  };

  const handleDelete = async (event, activityId) => {
    event.stopPropagation();
    try {
      console.log("Deleting activity:", activityId);
      await deleteActivity(activityId, currentUserId);
      setActivities((prevActivities) =>
        prevActivities.filter((activity) => activity.id !== activityId)
      );
      console.log("Activity deleted successfully");
    } catch (error) {
      console.error("Error deleting activity:", error.response?.data || error.message);
    }
  };

  useEffect(() => {
    fetchActivities();
  }, []);

  // Helper functions to inject custom colors based on the sport item type
  const getCardColorAccent = (type) => {
    const activityType = type?.toUpperCase();
    if (activityType === 'RUNNING') return '#ef4444'; // Red
    if (activityType === 'SWIMMING') return '#3b82f6'; // Blue
    if (activityType === 'CYCLING') return '#10b981'; // Green
    return '#6366f1'; 
  };

  const getActivityIcon = (type) => {
    const activityType = type?.toUpperCase();
    if (activityType === 'RUNNING') return '🏃';
    if (activityType === 'SWIMMING') return '🏊';
    if (activityType === 'CYCLING') return '🚴';
    return '💪';
  };

  return (
    <Box sx={{ flexGrow: 1, px: { xs: 1, sm: 2 }, py: 3 }}>
      
      {/* Cards Display Grid Section */}
      <Grid container spacing={3}>
        {activities.map((activity) => {
          const accentColor = getCardColorAccent(activity.type);
          const icon = getActivityIcon(activity.type);

          return (
            <Grid item xs={12} sm={6} md={4} lg={3} key={activity.id}>
              <Card 
                onClick={() => navigate(`/activities/${activity.id}`)}
                sx={{ 
                  cursor: 'pointer', 
                  height: '100%',
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'between',
                  borderRadius: '16px',
                  border: '1px solid #e7e7e9',
                  borderTop: `4px solid ${accentColor}`, // Visual category bar
                  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.02)',
                  transition: 'all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1)',
                  '&:hover': {
                    transform: 'translateY(-5px)',
                    boxShadow: '0 12px 20px rgba(0, 0, 0, 0.06)',
                    borderColor: '#d0d0d5'
                  }
                }}
              >
                <CardContent sx={{ p: 3, flexGrow: 1 }}>
                  {/* Header containing name and dynamic icon indicator */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2.5 }}>
                    <Typography 
                      variant="subtitle1" 
                      sx={{ 
                        fontWeight: 800, 
                        color: '#0d0c22', 
                        textTransform: 'uppercase', 
                        letterSpacing: '0.05em',
                        fontSize: '0.85rem' 
                      }}
                    >
                      {activity.type.toLowerCase()}
                    </Typography>
                    <Typography sx={{ fontSize: '1.25rem' }}>
                      {icon}
                    </Typography>
                  </Box>

                  {/* Clean row layouts tracking duration metrics */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', py: 1, borderBottom: '1px solid #f3f4f6' }}>
                    <Typography variant="body2" sx={{ color: '#6e6d7a', fontWeight: 500 }}>Duration</Typography>
                    <Typography variant="body2" sx={{ color: '#0d0c22', fontWeight: 700 }}>{activity.duration} mins</Typography>
                  </Box>

                  {/* Clean row layouts tracking calorie metrics */}
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', py: 1, mt: 0.5 }}>
                    <Typography variant="body2" sx={{ color: '#6e6d7a', fontWeight: 500 }}>Calories</Typography>
                    <Typography variant="body2" sx={{ color: '#0d0c22', fontWeight: 700 }}>{activity.caloriesBurned} kcal</Typography>
                  </Box>
                </CardContent>
                
                {/* Clean actions row with custom delete controls styling */}
                <Box sx={{ p: 2, pt: 0 }}>
                  <Button 
                    variant="text" 
                    size="small" 
                    onClick={(e) => handleDelete(e, activity.id)}
                    sx={{ 
                      width: '100%',
                      color: '#ff3b30',
                      backgroundColor: '#fff0f1',
                      fontWeight: 600,
                      textTransform: 'none',
                      borderRadius: '8px',
                      '&:hover': {
                        backgroundColor: '#ffe1e3',
                        color: '#e0241b'
                      }
                    }}
                  >
                    Delete Activity
                  </Button>
                </Box>
              </Card>
            </Grid>
          );
        })}  
      </Grid>
    </Box>
  );
}
