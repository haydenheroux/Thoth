// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.arm;

public interface ArmIO {
  public static class ArmIOValues {
    public double extensionLengthMeters = 0.0;
    public boolean extensionBrakeIsActive = true;

    public double rotationAngleRadians = 0.0;
    public boolean rotationBrakeIsActive = true;
  }

  /** Configures arm hardware to default. */
  public void configure();

  /**
   * Updates values with sensor information.
   *
   * @param values
   */
  public void updateValues(ArmIOValues values);

  /**
   * Sets the extension motor position.
   *
   * @param lengthMeters
   */
  public void setExtensionPosition(double lengthMeters);

  /**
   * Sets the extension motor setpoint.
   *
   * @param lengthMeters
   */
  public void setExtensionSetpoint(double lengthMeters);

  /**
   * Sets the extension motor voltage.
   *
   * @param volts
   */
  public void setExtensionVoltage(double volts);

  /**
   * Sets the extension brake.
   *
   * @param isActive
   */
  public void setExtensionBrake(boolean isActive);

  /** Disables extension motor. */
  public void setExtensionDisabled();

  /**
   * Sets the rotation motor position.
   *
   * @param angleRadians
   */
  public void setRotationPosition(double angleRadians);

  /**
   * Sets the rotation motor setpoint.
   *
   * @param angleRadians
   */
  public void setRotationSetpoint(double angleRadians);

  /**
   * Sets the rotation motor voltage.
   *
   * @param volts
   */
  public void setRotationVoltage(double volts);

  /**
   * Sets the rotation brake.
   *
   * @param isActive
   */
  public void setRotationBrake(boolean isActive);

  /** Disables rotation motor. */
  public void setRotationDisabled();
}
