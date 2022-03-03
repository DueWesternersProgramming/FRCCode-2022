// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Teleop.Intake;

import frc.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

/** An intake command that uses the driveSubsystem. */
public class DropIntake extends CommandBase {
  private final IntakeSubsystem m_intakeSubsystem;
  private boolean finished = false;

  /**
   * Creates a new StartIntake command.
   *
   * @param intakeSubsystem The subsystem used by this command.
   */
  public DropIntake(IntakeSubsystem intakeSubsystem) {
    m_intakeSubsystem = intakeSubsystem;
    addRequirements(m_intakeSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_intakeSubsystem.getIntakeLifterMotorPosition() < 70){
      m_intakeSubsystem.setIntakeLifted(false);
    }
    else {
      finished = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return finished;
  }
}
