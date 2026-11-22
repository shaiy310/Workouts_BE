DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS exercises;
DROP TABLE IF EXISTS user_exercise_weights;
DROP TABLE IF EXISTS activity_events;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS workout_sets;
DROP TABLE IF EXISTS workout_set_exercises;

CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(100) NOT NULL default '',
    password VARCHAR(255) NOT NULL default '',
    active INT NOT NULL DEFAULT 1,
    roles VARCHAR(50) NOT NULL DEFAULT '',
    permissions VARCHAR NOT NULL DEFAULT '',
    first_name VARCHAR(50) default '',
    last_name VARCHAR(50) default '',
    email VARCHAR(50) default ''
);

CREATE TABLE exercises (
    name VARCHAR(200) PRIMARY KEY,
    muscle_group VARCHAR(10) NOT NULL default 'Unknown',
    equipment VARCHAR(10) NOT NULL default 'BODYWEIGHT',
    weight DOUBLE PRECISION default 6.75,
    imagePath VARCHAR(100)
);

CREATE TABLE user_exercise_weights (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    exercise_name VARCHAR(200) NOT NULL,
    weight DOUBLE PRECISION NOT NULL CHECK (weight >= 0 AND weight <= 40),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, exercise_name)
);

CREATE TABLE activity_events (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('weight', 'workout')),
    event_date DATE NOT NULL DEFAULT CURRENT_DATE,

    -- Weight-change fields
    exercise_name VARCHAR(200),
    prev_weight DOUBLE PRECISION,
    new_weight DOUBLE PRECISION,

    -- Workout-completion fields
    workout_name VARCHAR(200),
    workout_description TEXT,

    CHECK (
    (event_type = 'weight'
         AND exercise_name IS NOT NULL
         AND prev_weight IS NOT NULL
         AND new_weight IS NOT NULL)
     OR
    (event_type = 'workout'
        AND workout_name IS NOT NULL)
    )
);
-- At most one workout event per user per day
CREATE UNIQUE INDEX uq_activity_events_user_workout_day
    ON activity_events (user_id, event_date)
    WHERE event_type = 'workout';

-- At most one weight event per user per exercise per day
CREATE UNIQUE INDEX uq_activity_events_user_exercise_weight_day
    ON activity_events (user_id, exercise_name, event_date)
    WHERE event_type = 'weight';

CREATE TABLE workouts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    duration INTEGER,
    description TEXT,
    notes TEXT,
    owner_id UUID REFERENCES users(id) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE workout_sets (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    workout_id UUID NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
    set_order INTEGER NOT NULL CHECK (set_order > 0),
    set_number INTEGER NOT NULL CHECK (set_number > 0),
    rest INTEGER NOT NULL DEFAULT 0,
    UNIQUE (workout_id, set_order)
);

CREATE TABLE workout_set_exercises (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    workout_set_id UUID NOT NULL REFERENCES workout_sets(id) ON DELETE CASCADE,
    exercise_order INTEGER NOT NULL CHECK (exercise_order > 0),
    exercise_name VARCHAR(200) NOT NULL,
    reps INTEGER,
    UNIQUE (workout_set_id, exercise_order)
);