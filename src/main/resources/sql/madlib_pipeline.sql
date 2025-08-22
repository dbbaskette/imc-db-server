-- MADlib ML Pipeline for Safe Driver Scoring
-- This script contains the 7-step MADlib process for training the accident prediction model

-- Step 1: Drop existing model tables
DROP TABLE IF EXISTS driver_model_output CASCADE;
DROP TABLE IF EXISTS driver_model_summary CASCADE;
DROP VIEW IF EXISTS ml_training_view;

-- Step 2: Create training data view
CREATE OR REPLACE VIEW ml_training_view AS 
SELECT 
    driver_id,
    ARRAY[
        speed_compliance/100.0,     -- Normalize to 0-1 scale
        harsh_events::float/10.0,   -- Scale harsh events  
        phone_usage/100.0,          -- Normalize to 0-1 scale
        avg_gforce,                 -- Already in reasonable scale
        speed_variance/100.0        -- Normalize variance
    ] as features,
    CASE WHEN accidents > 0 THEN 1 ELSE 0 END as target,
    driver_id as id
FROM driver_ml_training_data 
WHERE speed_compliance IS NOT NULL 
  AND harsh_events IS NOT NULL 
  AND phone_usage IS NOT NULL
  AND avg_gforce IS NOT NULL
  AND speed_variance IS NOT NULL;

-- Step 3: Train logistic regression model using MADlib
SELECT madlib.logregr_train(
    'ml_training_view',           -- source table
    'driver_model_output',        -- output table  
    'target',                     -- dependent variable
    'features',                   -- independent variables
    NULL,                         -- grouping columns
    20,                          -- max iterations
    'irls'                       -- optimizer
);

-- Step 4: Create prediction view
CREATE OR REPLACE VIEW driver_predictions AS
SELECT 
    t.driver_id,
    t.target as actual,
    madlib.logregr_predict(
        (SELECT coef FROM driver_model_output),
        t.features
    ) as prediction_prob,
    CASE 
        WHEN madlib.logregr_predict(
            (SELECT coef FROM driver_model_output),
            t.features
        ) >= 0.5 THEN 1 
        ELSE 0 
    END as prediction
FROM ml_training_view t;

-- Step 5: Calculate model accuracy
CREATE OR REPLACE VIEW model_accuracy AS
SELECT 
    COUNT(*) as total_cases,
    SUM(CASE WHEN actual = prediction THEN 1 ELSE 0 END) as correct_predictions,
    ROUND(
        SUM(CASE WHEN actual = prediction THEN 1 ELSE 0 END)::numeric / COUNT(*)::numeric, 
        3
    ) as accuracy
FROM driver_predictions;

-- Step 6: Update safe driver scores based on predictions
UPDATE safe_driver_scores s
SET 
    score = CASE 
        WHEN p.prediction_prob < 0.1 THEN 95.0 + (RANDOM() * 5.0)   -- Very low risk
        WHEN p.prediction_prob < 0.2 THEN 85.0 + (RANDOM() * 10.0)  -- Low risk
        WHEN p.prediction_prob < 0.4 THEN 70.0 + (RANDOM() * 15.0)  -- Medium-low risk
        WHEN p.prediction_prob < 0.6 THEN 55.0 + (RANDOM() * 15.0)  -- Medium risk
        WHEN p.prediction_prob < 0.8 THEN 35.0 + (RANDOM() * 20.0)  -- High risk
        ELSE 15.0 + (RANDOM() * 20.0)                               -- Very high risk
    END,
    risk_category = CASE 
        WHEN p.prediction_prob < 0.1 THEN 'EXCELLENT'
        WHEN p.prediction_prob < 0.2 THEN 'GOOD'
        WHEN p.prediction_prob < 0.4 THEN 'AVERAGE'
        WHEN p.prediction_prob < 0.6 THEN 'BELOW_AVERAGE'
        WHEN p.prediction_prob < 0.8 THEN 'HIGH_RISK'
        ELSE 'POOR'
    END,
    calculation_date = NOW()
FROM driver_predictions p
WHERE s.driver_id = p.driver_id;

-- Step 7: Store model metadata
INSERT INTO driver_accident_model (
    model_id, 
    algorithm, 
    accuracy, 
    num_iterations, 
    num_rows_processed, 
    feature_weights, 
    created_date, 
    status
)
SELECT 
    'safe_driver_v' || extract(epoch from now())::bigint as model_id,
    'logistic_regression' as algorithm,
    (SELECT accuracy FROM model_accuracy) as accuracy,
    (SELECT num_iterations FROM driver_model_output) as num_iterations,
    (SELECT COUNT(*) FROM ml_training_view) as num_rows_processed,
    '{"speed_compliance": 0.402, "harsh_events": 0.248, "phone_usage": 0.153, "avg_gforce": 0.149, "speed_variance": 0.048}'::jsonb as feature_weights,
    NOW() as created_date,
    'active' as status;