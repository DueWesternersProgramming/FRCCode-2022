// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.OIConstants;
import frc.robot.commands.Teleop.Drive.*;
import frc.robot.commands.Teleop.Intake.*;
import frc.robot.commands.Teleop.Shooter.*;
import frc.robot.subsystems.ClimberSubsystem;
//import frc.robot.commands.Auto.*;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.VisionSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
  private final ShooterSubsystem m_shooterSubsystem = new ShooterSubsystem();
  private final ClimberSubsystem m_climberSubsystem = new ClimberSubsystem();
  private final VisionSubsystem m_visionSubsystem = new VisionSubsystem();

  private Joystick leftDriveController = new Joystick(DriveConstants.kLeftControllerPort);
  private Joystick rightDriveController = new Joystick(DriveConstants.kRightControllerPort);
  private Joystick shootingController = new Joystick(DriveConstants.kShootingControllerPort);

  private final Command m_TankDrive = new TankDrive(m_driveSubsystem, 
  () -> leftDriveController.getRawAxis(1),
  () -> rightDriveController.getRawAxis(1));

  SendableChooser<Integer> m_chooser = new SendableChooser<>();

  String trajectory1JSON = "paths/YourPath.wpilib.json";
  String trajectory2JSON = "paths/YourPath2.wpilib.json";
  Path trajectoryPath1, trajectoryPath2;
  Trajectory trajectory = new Trajectory();

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();
    m_driveSubsystem.setDefaultCommand(new TankDrive(m_driveSubsystem,
    () -> leftDriveController.getRawAxis(1),
    () -> rightDriveController.getRawAxis(1)));

    Path trajectoryPath1 = Filesystem.getDeployDirectory().toPath().resolve(trajectory1JSON);
    Path trajectoryPath2 = Filesystem.getDeployDirectory().toPath().resolve(trajectory2JSON);

    m_chooser.setDefaultOption("Path 1", setTrajectoryChoice(1));
    m_chooser.addOption("Path 1", setTrajectoryChoice(1));
    Shuffleboard.getTab("Autonomous").add(m_chooser);
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(shootingController, OIConstants.kStartIntakeButton).whenPressed(new StartIntake(m_intakeSubsystem));
    new JoystickButton(shootingController, OIConstants.kStopIntakeButton).whenPressed(new StopIntake(m_intakeSubsystem));
    new JoystickButton(shootingController, OIConstants.kStartShooterButton).whenPressed(new StartShooter(m_shooterSubsystem));
    new JoystickButton(shootingController, OIConstants.kStopShooterButton).whenPressed(new StopShooter(m_shooterSubsystem));
    new JoystickButton(rightDriveController, OIConstants.kLiftIntakeButton).whenPressed(new LiftIntake(m_intakeSubsystem));
    new JoystickButton(rightDriveController, OIConstants.kDropIntakeButton).whenPressed(new DropIntake(m_intakeSubsystem));
  }

  public int setTrajectoryChoice(int path){
    if (path == 1){
      try {
        trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath1);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else if (path == 2){
      try {
        trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath2);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else {
      System.out.println("Trajectory Choice error.");
    }
    return path;
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // Create a voltage constraint to ensure we don't accelerate too fast
    var autoVoltageConstraint =
        new DifferentialDriveVoltageConstraint(
            new SimpleMotorFeedforward(
                DriveConstants.ksVolts,
                DriveConstants.kvVoltSecondsPerMeter,
                DriveConstants.kaVoltSecondsSquaredPerMeter),
            DriveConstants.kDriveKinematics,
            10);

    // Create config for trajectory
    TrajectoryConfig config =
        new TrajectoryConfig(
                DriveConstants.kMaxSpeedMetersPerSecond,
                DriveConstants.kMaxAccelerationMetersPerSecondSquared)
            // Add kinematics to ensure max speed is actually obeyed
            .setKinematics(DriveConstants.kDriveKinematics)
            // Apply the voltage constraint
            .addConstraint(autoVoltageConstraint);

    // An example trajectory to follow.  All units in meters.
    Trajectory exampleTrajectory =
        TrajectoryGenerator.generateTrajectory(
            // Start at the origin facing the +X direction
            new Pose2d(0, 0, new Rotation2d(0)),
            // Pass through these two interior waypoints, making an 's' curve path
            List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
            // End 3 meters straight ahead of where we started, facing forward
            new Pose2d(3, 0, new Rotation2d(0)),
            // Pass config
            config);

    RamseteCommand ramseteCommand =
        new RamseteCommand(
            exampleTrajectory,
            m_driveSubsystem::getPose,
            new RamseteController(DriveConstants.kRamseteB, DriveConstants.kRamseteZeta),
            new SimpleMotorFeedforward(
                DriveConstants.ksVolts,
                DriveConstants.kvVoltSecondsPerMeter,
                DriveConstants.kaVoltSecondsSquaredPerMeter),
            DriveConstants.kDriveKinematics,
            m_driveSubsystem::getWheelSpeeds,
            new PIDController(DriveConstants.kPDriveVel, 0, 0),
            new PIDController(DriveConstants.kPDriveVel, 0, 0),
            // RamseteCommand passes volts to the callback
            m_driveSubsystem::tankDriveVolts,
            m_driveSubsystem);

    // Reset odometry to the starting pose of the trajectory.
    m_driveSubsystem.resetOdometry(exampleTrajectory.getInitialPose());

    // Run path following command, then stop at the end.
    return ramseteCommand.andThen(() -> m_driveSubsystem.tankDriveVolts(0, 0));
  }
}
