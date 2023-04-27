// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.arm.Arm;
import frc.robot.arm.Arm.LockType;
import frc.robot.arm.DriveWithJoysticks;
import frc.robot.arm.ToGoal;
import frc.robot.intake.Claw;
import frc.robot.intake.SideIntake;
import frc.robot.swerve.Swerve;

public class RobotContainer {
  // Subsystems
  private final Arm arm;
  private final Compressor compressor;
  private final Claw claw;
  private final SideIntake sideIntake;
  private final Swerve swerve;

  // OI objects
  private final CommandXboxController driver = new CommandXboxController(0);
  private final CommandXboxController operator = new CommandXboxController(1);

  // Autonomous routine chooser
  private final SendableChooser<Command> autoChooser = new SendableChooser<Command>();

  public RobotContainer() {
    // Initialize subsystems
    arm = Arm.getInstance();
    compressor = new Compressor(PneumaticsModuleType.REVPH);
    claw = Claw.getInstance();
    sideIntake = SideIntake.getInstance();
    swerve = Swerve.getInstance();

    configureAutonomous();
    configureBindings();
    configureDefaultCommands();
    configureTriggers();
  }

  /** Configures the autonomous chooser with autonomous routines. */
  private void configureAutonomous() {}

  /** Configures bindings for driver and operator controllers. */
  private void configureBindings() {
    operator
        .y()
        .onTrue(Commands.runOnce(() -> arm.unlock(LockType.kBoth), arm))
        .whileTrue(
            new DriveWithJoysticks(
                () ->
                    MathUtil.applyDeadband(
                        operator.getRawAxis(XboxController.Axis.kRightY.value), 0.05),
                () ->
                    MathUtil.applyDeadband(
                        operator.getRawAxis(XboxController.Axis.kLeftY.value), 0.05)))
        .onFalse(Commands.runOnce(() -> arm.lock(LockType.kBoth), arm));

    operator.a().whileTrue(new ToGoal(new Arm.State(1.0, 0)));

    operator.b().whileTrue(new ToGoal(new Arm.State(1.5, Units.degreesToRadians(40))));

    operator
        .leftTrigger(0.5)
        .onTrue(claw.accept())
        .onFalse(claw.holdOrDisable());
    operator
        .rightTrigger(0.5)
        .onTrue(claw.eject())
        .onFalse(claw.disable());

    operator
        .leftBumper()
        .onTrue(sideIntake.accept())
        .onFalse(sideIntake.holdOrDisable());
    operator
        .rightBumper()
        .onTrue(sideIntake.eject())
        .onFalse(sideIntake.disable());
  }

  /** Configures default commands for each subsystem. */
  public void configureDefaultCommands() {}

  /** Configures triggers for arbitrary events. */
  private void configureTriggers() {}

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
