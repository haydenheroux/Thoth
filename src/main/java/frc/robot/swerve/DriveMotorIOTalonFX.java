package frc.robot.swerve;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.controls.NeutralOut;
import com.ctre.phoenix6.controls.StaticBrake;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.lib.hardware.ConfigurationApplier;
import frc.lib.math.Conversions;
import frc.robot.Constants;
import frc.robot.Constants.Physical;
import frc.robot.Constants.Swerve;

public class DriveMotorIOTalonFX implements DriveMotorIO {

  private final TalonFX motor;
  private final SimpleMotorFeedforward feedforward;

  private final StatusSignal<Double> position, velocity;

  private final VelocityVoltage velocityController;

  public DriveMotorIOTalonFX(int id, String canbus) {
    motor = new TalonFX(id, canbus);
    position = motor.getPosition();
    velocity = motor.getVelocity();

    double kv = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_SPEED;
    double ka = Constants.NOMINAL_VOLTAGE / Constants.Swerve.MAX_ACCELERATION;

    feedforward = new SimpleMotorFeedforward(0.0, kv, ka);

    velocityController = new VelocityVoltage(0);
  }

  @Override
  public void configure() {
    ConfigurationApplier.apply(Swerve.DRIVE_CONFIG, motor);

    position.setUpdateFrequency(100);
    velocity.setUpdateFrequency(100);
  }

  @Override
  public void updateValues(DriveMotorIOValues values) {
    values.positionMeters =
        Conversions.General.toMeters(position.getValue(), Physical.WHEEL_CIRCUMFERENCE);
    values.velocityMetersPerSecond =
        Conversions.General.toMeters(velocity.getValue(), Physical.WHEEL_CIRCUMFERENCE);
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
