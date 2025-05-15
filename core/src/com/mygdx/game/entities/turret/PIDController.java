package com.mygdx.game.entities.turret;

public class PIDController {
    private final float kp;
    private final float ki;
    private final float kd;
    private float integral, previousError;

    public PIDController(float kp, float ki, float kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.integral = 0;
        this.previousError = 0;
    }

    public float update(float setpoint, float measured, float deltaTime) {
        float error = setpoint - measured;
        integral += error * deltaTime;
        float derivative = (error - previousError) / deltaTime;
        previousError = error;
        return kp * error + ki * integral + kd * derivative;
    }
}
