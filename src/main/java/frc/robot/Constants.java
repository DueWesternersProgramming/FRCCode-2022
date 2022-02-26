// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    public static final class DriveConstants{
        public static final int kLeft1MotorPort = 2;
        public static final int kLeft2MotorPort = 3;
        public static final int kRight1MotorPort = 4;
        public static final int kRight2MotorPort = 5;

        public static final double kSpeedMultiplier = -0.5;
    }

    public static final class IntakeConstants{
        public static final int kIntakeMotorPort = 6;
        public static final int kIntakeServoPort = 1;
        public static final int kIntakeLiftPort = 7;

        public static final double kIntakeSpeed = 0.5;
    }

    public static final class ShooterConstants{
        public static final int kShooterMotorPort = 8;
        public static final int kShooterFeedPort = 9;

        public static final double kShooterSpeed = -0.35;
        public static final double kFeederSpeed = -0.2;
    }

    public static final class ClimberConstants{
        public static final int kClimber1MotorPort = 10;
        public static final int kClimber2MotorPort = 11;
    }

    public static final class OIConstants{
        public static final int kStartIntakeButton = 3;
        public static final int kStopIntakeButton = 2;
        public static final int kStartShooterButton = 6;
        public static final int kStopShooterButton = 5;
        public static final int kStartFeederButton = 1;
        public static final int kStopFeederButton = 4;
    }

}