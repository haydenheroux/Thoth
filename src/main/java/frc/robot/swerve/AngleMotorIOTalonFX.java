package frc.robot.swerve;

import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import edu.wpi.first.wpilibj.Timer;
import frc.lib.math.Conversions;
import frc.robot.Constants;
import frc.robot.Constants.Swerve;
import frc.robot.Constants.Swerve.Angle;

public class AngleMotorIOTalonFX implements AngleMotorIO {

  private final WPI_TalonFX motor;

  public AngleMotorIOTalonFX(int id, String canbus) {
    motor = new WPI_TalonFX(id, canbus);
  }

  @Override
  public void configure() {
    motor.configFactoryDefault();
    motor.setSensorPhase(true); // TODO
    motor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 30); // TODO
    motor.configNeutralDeadband(0.001); // TODO
    motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 250); // TODO

    TalonFXConfiguration config = new TalonFXConfiguration();

    config.slot0.kP = Angle.KP;
    config.slot0.kD = Angle.KD;

    config.voltageCompSaturation = Constants.NOMINAL_VOLTAGE;
    config.supplyCurrLimit.currentLimit = Angle.CURRENT_LIMIT;
    config.supplyCurrLimit.triggerThresholdCurrent = Angle.CURRENT_LIMIT;
    config.supplyCurrLimit.triggerThresholdTime = 0;
    config.supplyCurrLimit.enable = true;

    config.closedloopRamp = Angle.RAMP_TIME;

    Timer.delay(1);
    motor.setInverted(Swerve.INVERTED);

    motor.configAllSettings(config, 250);
  }

  @Override
  public void updateValues(AngleMotorIOValues values) {
    values.angleRotations =
        Conversions.TalonFX.Position.toRotations(
            motor.getSelectedSensorPosition(), Angle.GEAR_RATIO);
    values.omegaRotationsPerSecond =
        Conversions.TalonFX.Velocity.toRPS(motor.getSelectedSensorPosition(), Angle.GEAR_RATIO);
  }

  @Override
  public void setPosition(double angleRotations) {
    // TODO
    // angleRotations = angleRotations < 0 ? (angleRotations % 2 * Math.PI) + 2 * Math.PI :
    // angleRotations;
    motor.setSelectedSensorPosition(
        Conversions.TalonFX.Position.fromRotations(angleRotations, Angle.GEAR_RATIO), 0, 250);
  }

  @Override
  public void setSetpoint(double angleRotations) {
    motor.set(
        TalonFXControlMode.Position,
        Conversions.TalonFX.Position.fromRotations(angleRotations, Angle.GEAR_RATIO),
        DemandType.ArbitraryFeedForward,
        0.0); // TODO
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