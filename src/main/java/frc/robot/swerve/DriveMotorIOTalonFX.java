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
    config.slot0.kI = Drive.KI;
    config.slot0.kD = Drive.KD;
    config.slot0.kF = Drive.KF;
    config.slot0.integralZone = Drive.INTEGRAL_ZONE;
    config.slot0.closedLoopPeakOutput = Drive.CLOSED_LOOP_PEAK_OUTPUT;

    config.voltageCompSaturation = Constants.NOMINAL_VOLTAGE;
    config.supplyCurrLimit.currentLimit = Drive.CURRENT_LIMIT;
    config.supplyCurrLimit.triggerThresholdCurrent = Drive.PEAK_CURRENT;
    config.supplyCurrLimit.triggerThresholdTime = Drive.PEAK_TIME;
    config.supplyCurrLimit.enable = Drive.CURRENT_LIMIT_ENABLED;

    config.closedloopRamp = Drive.CLOSED_LOOP_RAMP_TIME;
    config.openloopRamp = Drive.OPEN_LOOP_RAMP_TIME;

    Timer.delay(1);
    motor.setInverted(Drive.INVERTED);

    motor.configAllSettings(config, 250);
  }

  @Override
  public void updateValues(DriveMotorIOValues values) {
    values.positionMeters =
        Conversions.TalonFX.Position.toMeters(
            motor.getSelectedSensorPosition(), Drive.WHEEL_CIRCUMFERENCE, Drive.GEAR_RATIO);
    values.velocityMetersPerSecond =
        Conversions.TalonFX.Velocity.toMPS(
            motor.getSelectedSensorVelocity(), Drive.WHEEL_CIRCUMFERENCE, Drive.GEAR_RATIO);
  }

  @Override
  public void setPosition(double distanceMeters) {
    motor.setSelectedSensorPosition(
        Conversions.TalonFX.Position.fromMeters(
            distanceMeters, Drive.WHEEL_CIRCUMFERENCE, Drive.GEAR_RATIO),
        0,
        250);
  }

  @Override
  public void setSetpoint(double distanceMeters) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setSetpoint'");
  }

  @Override
  public void setVelocitySetpoint(double velocityMetersPerSecond) {
    motor.set(
        TalonFXControlMode.Velocity,
        Conversions.TalonFX.Velocity.fromMPS(
            velocityMetersPerSecond, Drive.WHEEL_CIRCUMFERENCE, Drive.GEAR_RATIO),
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
