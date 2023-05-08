// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.swerve;

public interface DriveMotorIO {
  public static class DriveMotorIOValues {
    public double positionMeters = 0.0;
    public double velocityMetersPerSecond = 0.0;
  }

  public void configure();

  public void updateValues(DriveMotorIOValues values);

  public void setPosition(double distanceMeters);

  public void setSetpoint(double distanceMeters);

  public void setVelocitySetpoint(double velocityMetersPerSecond);

  public void setVoltage(double volts);

  public void setBrake(boolean isActive);
}