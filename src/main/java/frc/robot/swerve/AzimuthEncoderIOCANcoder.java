package frc.robot.swerve;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import frc.lib.hardware.ConfigurationApplier;
import frc.robot.Constants.Swerve;

/** Implements azimuth encoder behaviors for a CANCoder. */
public class AzimuthEncoderIOCANcoder implements AzimuthEncoderIO {

  private final CANcoder encoder;
  private final double magnetOffset;

  private final StatusSignal<Double> absolutePosition;

  /**
   * Constructs a new CANcoder azimuth encoder.
   *
   * @param id the CAN ID of the CANcoder.
   * @param canbus the name of the CAN bus for the CANcoder.
   * @param offsetRotations the magnet offset to apply to the CANcoder, in rotations.
   */
  public AzimuthEncoderIOCANcoder(int id, String canbus, double offsetRotations) {
    encoder = new CANcoder(id, canbus);

    absolutePosition = encoder.getAbsolutePosition();

    magnetOffset = offsetRotations;
  }

  @Override
  public void configure() {
    ConfigurationApplier.apply(Swerve.AZIMUTH_CONFIG, encoder);

    // TODO Don't overwrite previous config
    MagnetSensorConfigs offset = new MagnetSensorConfigs();
    offset.MagnetOffset = magnetOffset;

    ConfigurationApplier.apply(offset, encoder);

    absolutePosition.setUpdateFrequency(100);
  }

  @Override
  public void updateValues(AzimuthEncoderIOValues values) {
    values.angleRotations = absolutePosition.getValue();
  }
}