// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.arm;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.Constants.Arm.Positions;
import java.util.LinkedList;
import java.util.Queue;

public class ArmTrajectory {

  private final Queue<ArmPosition> setpoints;

  public ArmTrajectory(ArmPosition start, ArmPosition end) {
    setpoints = new LinkedList<ArmPosition>();

    Rotation2d aboveGridAngle = Positions.ABOVE_GRID.getAngle();

    if (directTrajectoryIntersectsGrid(start, end)) {
      setpoints.add(start.withAngle(aboveGridAngle));
      setpoints.add(start.withAngle(aboveGridAngle).withLengthOf(end));
    }

    setpoints.add(end);
  }

  public ArmPosition get() {
    return setpoints.element();
  }

  public ArmPosition next() {
    boolean hasNext = setpoints.size() > 1;

    if (hasNext) {
      setpoints.remove();
    }

    return get();
  }

  private boolean directTrajectoryIntersectsGrid(ArmPosition start, ArmPosition end) {
    Rotation2d startAngle, endAngle;

    if (start.isBelow(end)) {
      startAngle = start.getAngle();
      endAngle = end.getAngle();
    } else {
      startAngle = end.getAngle();
      endAngle = start.getAngle();
    }

    double worstCaseLength = Math.max(start.getNorm(), end.getNorm());

    for (double percent = 0.0; percent < 1.0; percent += 0.01) {
      Rotation2d angle = startAngle.interpolate(endAngle, percent);

      ArmPosition worstCase = new ArmPosition(worstCaseLength, angle);

      if (ArmKinematics.isIntersectingGrid(worstCase)) {
        return true;
      }
    }

    return false;
  }
}
