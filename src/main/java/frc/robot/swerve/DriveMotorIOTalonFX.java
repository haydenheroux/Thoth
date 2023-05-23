package frc.robot.swerve;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.NeutralOut;
import com.ctre.phoenix6.controls.StaticBrake;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.lib.math.Conversions;
import frc.robot.Constants;
import frc.robot.Constants.Physical;
import frc.robot.Constants.Swerve.Drive;

public class DriveMotorIOTalonFX implements DriveMotorIO {

  private final TalonFX motor;
  private final SimpleMotorFeedforward feedforward;

  private final VelocityVoltage velocityController;

  public DriveMotorIOTalonFX(int id, String canbus) {
    motor = new TalonFX(id, canbus);

    double kv = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_SPEED;
    double ka = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_ACCELERATION;

    feedforward = new SimpleMotorFeedforward(0.0, kv, ka);

    velocityController = new VelocityVoltage(0);
  }

  @Override
  public void configure() {
    TalonFXConfiguration config = new TalonFXConfiguration();

    // TODO
    config.MotorOutput.Inverted = InvertedValue.Clockwise_Positive;

    config.Slot0.kP = Drive.KP;

    config.CurrentLimits.StatorCurrentLimit = Drive.CURRENT_LIMIT;
    config.CurrentLimits.StatorCurrentLimitEnable = true;

    config.ClosedLoopRamps.VoltageClosedLoopRampPeriod = Drive.RAMP_TIME;

    config.Feedback.SensorToMechanismRatio = Drive.GEAR_RATIO;

    /*
     * https://github.com/TitaniumTitans/2023ChargedUp/blob/0306f0274d170ba5cd87808f60e1d64475917b67/src/main/java/frc/robot/subsystems/swerve/module/FalconProModule.java#L201
     */
    StatusCode status;
    do {
      status = motor.getConfigurator().apply(config);
    } while (!status.isOK());

    motor.getPosition().setUpdateFrequency(100);
    motor.getVelocity().setUpdateFrequency(100);
  }

  @Override
  public void updateValues(DriveMotorIOValues values) {
    values.positionMeters =
        Conversions.General.toMeters(motor.getPosition().getValue(), Physical.WHEEL_CIRCUMFERENCE);
    values.velocityMetersPerSecond =
        Conversions.General.toMeters(motor.getVelocity().getValue(), Physical.WHEEL_CIRCUMFERENCE);
  }

  @Override
  public void setPosition(double distanceMeters) {
    double rotations =
        Conversions.General.toRotations(distanceMeters, Physical.WHEEL_CIRCUMFERENCE);

    motor.setRotorPosition(rotations);
  }

  @Override
  public void setVelocitySetpoint(double velocityMetersPerSecond) {
    double rotationsPerSecond =
        Conversions.General.toRotations(velocityMetersPerSecond, Physical.WHEEL_CIRCUMFERENCE);

    motor.setControl(
        velocityController
            .withVelocity(rotationsPerSecond)
            .withFeedForward(feedforward.calculate(velocityMetersPerSecond)));
  }

  @Override
  public void setBrake(boolean isActive) {
    if (isActive) {
      motor.setControl(new StaticBrake());
    } else {
      motor.setControl(new NeutralOut());
    }
  }
}
