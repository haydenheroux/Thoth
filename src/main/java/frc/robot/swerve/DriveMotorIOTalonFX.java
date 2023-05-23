package frc.robot.swerve;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.math.Conversions;
import frc.robot.Constants;
import frc.robot.Constants.Physical;
import frc.robot.Constants.Swerve;
import frc.robot.Constants.Swerve.Drive;

public class DriveMotorIOTalonFX implements DriveMotorIO {

  private final WPI_TalonFX motor;
  private final SimpleMotorFeedforward feedforward;

  public DriveMotorIOTalonFX(int id, String canbus) {
    motor = new WPI_TalonFX(id, canbus);

    double kv = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_SPEED;
    double ka = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_ACCELERATION;

    feedforward = new SimpleMotorFeedforward(0.0, kv, ka);
  }

  @Override
  public void configure() {
    motor.configFactoryDefault();
    motor.setSensorPhase(true); // TODO
    motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 30); // TODO
    motor.configNeutralDeadband(0.001); // TODO
    motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 250); // TODO

    TalonFXConfiguration config = new TalonFXConfiguration();

    config.slot0.kP = Drive.KP;
    config.slot0.kD = Drive.KD;

    config.voltageCompSaturation = Constants.NOMINAL_VOLTAGE;
    config.supplyCurrLimit.currentLimit = Drive.CURRENT_LIMIT;
    config.supplyCurrLimit.triggerThresholdCurrent = Drive.CURRENT_LIMIT;
    config.supplyCurrLimit.triggerThresholdTime = 0;
    config.supplyCurrLimit.enable = true;

    config.closedloopRamp = Drive.RAMP_TIME;

    Timer.delay(1);
    motor.setInverted(Swerve.INVERTED);

    motor.configAllSettings(config, 250);
  }

  @Override
  public void updateValues(DriveMotorIOValues values) {
    double positionRotations =
        Conversions.TalonFX.Position.toRotations(
            motor.getSelectedSensorPosition(), Drive.GEAR_RATIO);

    double velocityRotationsPerSecond =
        Conversions.TalonFX.Velocity.toRPS(motor.getSelectedSensorVelocity(), Drive.GEAR_RATIO);

    values.positionMeters =
        Conversions.General.toMeters(positionRotations, Physical.WHEEL_CIRCUMFERENCE);
    values.velocityMetersPerSecond =
        Conversions.General.toMeters(velocityRotationsPerSecond, Physical.WHEEL_CIRCUMFERENCE);
  }

  @Override
  public void setPosition(double distanceMeters) {
    double rotations =
        Conversions.General.toRotations(distanceMeters, Physical.WHEEL_CIRCUMFERENCE);

    motor.setSelectedSensorPosition(
        Conversions.TalonFX.Position.fromRotations(rotations, Drive.GEAR_RATIO), 0, 250);
  }

  @Override
  public void setSetpoint(double distanceMeters) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setSetpoint'");
  }

  @Override
  public void setVelocitySetpoint(double velocityMetersPerSecond) {
    double rotationsPerSecond =
        Conversions.General.toRotations(velocityMetersPerSecond, Physical.WHEEL_CIRCUMFERENCE);

    motor.set(
        TalonFXControlMode.Velocity,
        Conversions.TalonFX.Velocity.fromRPS(rotationsPerSecond, Drive.GEAR_RATIO),
        DemandType.ArbitraryFeedForward,
        feedforward.calculate(velocityMetersPerSecond));
  }

  @Override
  public void setVoltage(double volts) {
    motor.setVoltage(volts);
  }

  @Override
  public void setBrake(boolean isActive) {
    motor.setNeutralMode(isActive ? NeutralMode.Brake : NeutralMode.Coast);
  }
}
