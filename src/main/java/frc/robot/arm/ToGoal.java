// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.arm;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.arm.Arm.Type;

public class ToGoal extends CommandBase {

  private final Arm arm;
  private final ArmPosition goal;

  private ArmTrajectory trajectory;

  public ToGoal(Arm arm, ArmPosition goal) {
    addRequirements(arm);

    this.arm = arm;
    this.goal = goal;
  }

  @Override
  public void initialize() {
    arm.unlock(Type.kBoth);
    trajectory = new ArmTrajectory(arm.getPosition(), goal);

    arm.setGoal(goal);
  }

  @Override
  public void execute() {
    ArmPosition setpoint = trajectory.get();

    if (arm.at(setpoint)) {
      setpoint = trajectory.next();
    }

    arm.setSetpoint(setpoint);
  }

  @Override
  public void end(boolean interrupted) {
    arm.disable(Type.kBoth);
    arm.lock(Type.kBoth);
  }

  @Override
  public boolean isFinished() {
    return arm.at(goal);
  }
}