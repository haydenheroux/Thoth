// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import frc.lib.math.Conversions;
import frc.robot.Constants;

public class ArmIOTalonFXBase implements ArmIO {

  protected final WPI_TalonFX extensionMotor, rotationMotor;
  private final Solenoid extensionBrake, rotationBrake;

  public ArmIOTalonFXBase() {
    extensionMotor = new WPI_TalonFX(Constants.Arm.Extension.CAN_ID);
    extensionBrake =
        new Solenoid(PneumaticsModuleType.REVPH, Constants.Arm.Extension.BRAKE_CHANNEL);

    rotationMotor = new WPI_TalonFX(Constants.Arm.Rotation.CAN_ID);
    rotationBrake = new Solenoid(PneumaticsModuleType.REVPH, Constants.Arm.Rotation.BRAKE_CHANNEL);
  }

  @Override
  public void configure() {
    extensionMotor.setInverted(false);
    extensionMotor.setNeutralMode(NeutralMode.Brake);

    rotationMotor.setInverted(true);
    rotationMotor.setNeutralMode(NeutralMode.Brake);
  }

  @Override
  public void updateValues(ArmIOValues values) {
    values.extensionLengthMeters = getExtensionPosition();
    values.extensionBrakeIsActive = extensionBrake.get();
    values.rotationAngleRadians = getRotationPosition();
    values.rotationBrakeIsActive = rotationBrake.get();
  }

  @Override
  public void setExtensionPosition(double lengthMeters) {
    double units =
        Conversions.TalonFX.fromMeters(
            lengthMeters,
            Constants.Arm.Extension.DISTANCE_PER_ROTATION,
            Constants.Arm.Extension.GEAR_RATIO);
    extensionMotor.setSelectedSensorPosition(units);
  }

  @Override
  public void setExtensionSetpoint(double lengthMeters) {}

  @Override
  public void setExtensionVoltage(double volts) {
    rotationMotor.set(ControlMode.PercentOutput, volts / Constants.NOMINAL_VOLTAGE);
  }

  @Override
  public void setExtensionBrake(boolean isActive) {
    extensionBrake.set(isActive);
  }

  @Override
  public void setExtensionDisabled() {
    setExtensionVoltage(0);
  }

  @Override
  public void setRotationPosition(double angleRadians) {
    double units = Conversions.TalonFX.fromRadians(angleRadians, Constants.Arm.Rotation.GEAR_RATIO);
    rotationMotor.setSelectedSensorPosition(units);
  }

  @Override
  public void setRotationSetpoint(double angleRadians) {}

  @Override
  public void setRotationVoltage(double volts) {
    rotationMotor.set(ControlMode.PercentOutput, volts / Constants.NOMINAL_VOLTAGE);
    ;
  }

  @Override
  public void setRotationBrake(boolean isActive) {
    rotationBrake.set(isActive);
  }

  @Override
  public void setRotationDisabled() {
    setRotationVoltage(0);
  }

  protected double getExtensionPosition() {
    return Conversions.TalonFX.toMeters(
        extensionMotor.getSelectedSensorPosition(),
        Constants.Arm.Extension.DISTANCE_PER_ROTATION,
        Constants.Arm.Extension.GEAR_RATIO);
  }

  protected double getRotationPosition() {
    return Conversions.TalonFX.toRadians(
        rotationMotor.getSelectedSensorPosition(), Constants.Arm.Rotation.GEAR_RATIO);
  }
}
