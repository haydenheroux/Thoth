// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.intake;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class SideIntake extends SubsystemBase {
  // Singleton instance
  private static SideIntake instance = null;

  /** Creates a new SideIntake. */
  private SideIntake() {}

  public static SideIntake getInstance() {
    if (instance == null) {
      instance = new SideIntake();
    }
    return instance;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
