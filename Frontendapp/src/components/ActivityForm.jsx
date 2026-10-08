import { Button, FormControl, InputLabel, MenuItem, TextField, Box, Select, Grid, Typography} from '@mui/material';
import React, { useState } from 'react';
import { motion } from 'framer-motion';

export default function ActivityForm({ onActivitysAdded, addedActivity }) {
    const [activity, setActivity] = useState({ type: 'RUNNING', duration: '', caloriesBurned: '', additionalMetrics: {} });
    
    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            await addedActivity(activity);
            onActivitysAdded();
            setActivity({ type: 'RUNNING', duration: '', caloriesBurned: '' });
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <Box 
            component="form" 
            onSubmit={handleSubmit}
            sx={{ 
                mb: 6,
                p: { xs: 3, sm: 4 },
                backgroundColor: '#ffffff',
                borderRadius: '16px',
                border: '1px solid #e7e7e9',
                boxShadow: '0 4px 12px rgba(0, 0, 0, 0.03)'
            }}
        >
            <motion.div
                initial={{ opacity: 0, y: 15 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, ease: 'easeOut' }}
            >
                {/* Form Title Headline for Modern Context */}
                <Box sx={{ mb: 3 }}>
                    <Typography variant="subtitle2" sx={{ textTransform: 'uppercase', color: '#6e6d7a', fontWeight: 700, tracking: '0.05em', mb: 0.5 }}>
                        Quick Log
                    </Typography>
                    <Typography variant="h6" sx={{ fontWeight: 800, color: '#0d0c22' }}>
                        Track New Activity
                    </Typography>
                </Box>

                {/* Grid Layout Container */}
                <Grid container spacing={2.5} alignItems="flex-end">
                    
                    {/* 1. Activity Type Select Dropdown */}
                    <Grid item xs={12} sm={6} md={3}>
                        <FormControl fullWidth size="small">
                            <InputLabel id="activity-type-label">Activity Type</InputLabel>
                            <Select
                                labelId="activity-type-label"
                                label="Activity Type"
                                value={activity.type}
                                onChange={(e) => setActivity({ ...activity, type: e.target.value })}
                                sx={{ borderRadius: '8px' }}
                            >
                                <MenuItem value="RUNNING">🏃 Running</MenuItem>
                                <MenuItem value="CYCLING">🚴 Cycling</MenuItem>
                                <MenuItem value="SWIMMING">🏊 Swimming</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* 2. Duration Input Field */}
                    <Grid item xs={12} sm={6} md={3}>
                        <TextField 
                            fullWidth
                            size="small"
                            label="Duration (minutes)"
                            type="number"
                            value={activity.duration}
                            onChange={(e) => setActivity({ ...activity, duration: e.target.value })}
                            InputProps={{ style: { borderRadius: '8px' } }}
                        />
                    </Grid>

                    {/* 3. Calories Input Field */}
                    <Grid item xs={12} sm={6} md={3}>
                        <TextField 
                            fullWidth
                            size="small"
                            label="Calories Burned"
                            type="number"
                            value={activity.caloriesBurned}
                            onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })}
                            InputProps={{ style: { borderRadius: '8px' } }}
                        />
                    </Grid>

                    {/* 4. Action Submission Button */}
                    <Grid item xs={12} sm={6} md={3}>
                        <Button 
                            type="submit" 
                            variant="contained" 
                            fullWidth
                            sx={{
                                height: '40px', // Standardizes size height matching fields perfectly
                                backgroundColor: '#6366f1',
                                fontWeight: 600,
                                textTransform: 'none',
                                borderRadius: '8px',
                                boxShadow: '0 4px 10px rgba(99, 102, 241, 0.2)',
                                '&:hover': {
                                    backgroundColor: '#4f46e5',
                                    boxShadow: '0 6px 14px rgba(99, 102, 241, 0.3)',
                                }
                            }}
                        >
                            Add Activity
                        </Button>
                    </Grid>

                </Grid>
            </motion.div>
        </Box>
    );
}
