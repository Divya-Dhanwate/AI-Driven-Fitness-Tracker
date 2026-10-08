import { CardContent, Divider, Typography, Box, Card, Grid } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { getActivityDetails } from '../services/api';

export default function ActivityDetails() {
  const { id } = useParams();
  const [data, setData] = useState(null);

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        console.log("fetching activity with ID:", id);
        const response = await getActivityDetails(id);
        console.log("Activity details response:", response.data);
        setData(response.data);
      } catch (error) {
        console.error('Error fetching activity details:', error);
      }
    };

    if (id) {
      fetchActivityDetail();
    }
  }, [id]);

  if (!data) {
    return (
      <Typography sx={{ p: 4, textAlign: 'center', color: '#6e6d7a', fontWeight: 500 }}>
        Loading your AI plan...
      </Typography>
    );
  }

  const renderItems = (items) => {
    if (!items) return null;

    if (Array.isArray(items)) {
      return items.map((item, index) => (
        <Typography
          key={index}
          sx={{
            pl: 1,
            mb: 1.5,
            lineHeight: 1.6,
            color: '#2d3748',
            fontSize: '0.95rem'
          }}
        >
          • {item}
        </Typography>
      ));
    }

    return (
      <Typography
        sx={{
          pl: 1,
          mb: 1.5,
          lineHeight: 1.6,
          color: '#2d3748',
          fontSize: '0.95rem'
        }}
      >
        • {items}
      </Typography>
    );
  };

  return (
    <Box
      sx={{
        maxWidth: 1200, // Expanded width for side-by-side desktop view grid configurations
        mx: 'auto',
        p: { xs: 2, sm: 4 }, // Fluid padding: 16px on mobile, 32px on tablet/desktop
        fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'
      }}
    >
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: 'easeOut' }}
      >
        
        {/* Modern Dribbble Style Header Banner */}
        <Box sx={{ mb: 4 }}>
          <Typography 
            variant="h4" 
            sx={{ fontWeight: 800, color: '#0d0c22', tracking: '-0.02em', mb: 0.5 }}
          >
            Action Plan Analysis
          </Typography>
          <Typography variant="body2" sx={{ color: '#6e6d7a', fontWeight: 500 }}>
            Smart health recommendations powered by Active Pulse AI
          </Typography>
        </Box>

        {/* AI Overview Recommendation Component */}
        <Card
          sx={{
            mb: 4,
            background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)', // Premium Purple Gradient
            borderRadius: '16px',
            boxShadow: '0 10px 25px -5px rgba(99, 102, 241, 0.3)',
            color: '#ffffff',
            position: 'relative',
            overflow: 'hidden'
          }}
        >
          <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, mb: 2 }}>
              <Box 
                sx={{ 
                  background: 'rgba(255, 255, 255, 0.2)', 
                  px: 2, py: 0.5, 
                  borderRadius: '20px', 
                  fontSize: '0.75rem', 
                  fontWeight: 700, 
                  textTransform: 'uppercase',
                  letterSpacing: '0.05em'
                }}
              >
                ✨ AI Coach Overview
              </Box>
            </Box>
            <Typography
              sx={{
                fontStyle: 'normal',
                fontSize: { xs: '1.1rem', sm: '1.25rem' },
                lineHeight: 1.6,
                fontWeight: 500
              }}
            >
              "{data.recommendations}"
            </Typography>
          </CardContent>
        </Card>

        {/* Responsive Content Grid Architecture */}
        <Grid container spacing={3}>
          
          {/* Left Column: Activity Card */}
          {data.activityType && (
            <Grid item xs={12} md={4}>
              <Card 
                sx={{ 
                  borderRadius: '16px', 
                  border: '1px solid #e7e7e9',
                  boxShadow: '0 4px 12px rgba(0, 0, 0, 0.03)',
                  height: '100%',
                  background: '#ffffff',
                  borderTop: '5px solid #6366f1' // Connected color visual indicator
                }}
              >
                <CardContent sx={{ p: 3, textAlign: 'center' }}>
                  <Typography variant="subtitle2" sx={{ textTransform: 'uppercase', color: '#6e6d7a', fontWeight: 700, tracking: '0.05em', mb: 1 }}>
                    Logged Activity
                  </Typography>
                  <Typography
                    variant="h3"
                    sx={{
                      textTransform: 'capitalize',
                      fontWeight: 800,
                      color: '#0d0c22',
                      mt: 2
                    }}
                  >
                    {data.activityType === 'running' && '🏃'}
                    {data.activityType === 'swimming' && '🏊'}
                    {data.activityType === 'cycling' && '🚴'}
                    <Box component="div" sx={{ mt: 1 }}>{data.activityType}</Box>
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          )}

          {/* Right Column: Dynamic Performance Analytics */}
          <Grid item xs={12} md={data.activityType ? 8 : 12}>
            <Card 
              sx={{ 
                borderRadius: '16px', 
                border: '1px solid #e7e7e9',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.03)',
                background: '#ffffff'
              }}
            >
              <CardContent sx={{ p: { xs: 3, sm: 4 } }}>
                
                {/* Suggestions Data Rendering Block */}
                {data.suggestions && (Array.isArray(data.suggestions) ? data.suggestions.length > 0 : true) && (
                  <Box sx={{ mb: 3 }}>
                    <Typography variant="h6" sx={{ color: '#10b981', fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                      💡 Suggestions
                    </Typography>
                    {renderItems(data.suggestions)}
                    <Divider sx={{ my: 2.5, borderColor: '#f3f4f6' }} />
                  </Box>
                )}

                {/* Improvement Areas Data Rendering Block */}
                {data.improvement && (Array.isArray(data.improvement) ? data.improvement.length > 0 : true) && (
                  <Box sx={{ mb: 3 }}>
                    <Typography variant="h6" sx={{ color: '#f59e0b', fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                      📈 Areas of Improvement
                    </Typography>
                    {renderItems(data.improvement)}
                    <Divider sx={{ my: 2.5, borderColor: '#f3f4f6' }} />
                  </Box>
                )}

                {/* Safety Precautions Data Rendering Block */}
                {data.safety && (Array.isArray(data.safety) ? data.safety.length > 0 : true) && (
                  <Box>
                    <Typography variant="h6" sx={{ color: '#ef4444', fontWeight: 700, display: 'flex', alignItems: 'center', gap: 1, mb: 1.5 }}>
                      🛡️ Safety Precautions
                    </Typography>
                    {renderItems(data.safety)}
                  </Box>
                )}

              </CardContent>
            </Card>
          </Grid>

        </Grid>
      </motion.div>
    </Box>
  );
}
