/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.team670.mustanglib.commands.drive.teleop.tank.XboxRocketLeague;

import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.subsystems.drivebase.TankDrive;
/*
* Note: This is for tank drive
* uses a singles joystick to control the left and right sides of the tank drive
* Forward back for froward reverse and twist to turn
*/
public class SingleJoystickDrive extends Command implements MustangCommand {
    private TankDrive driveBase;
    private Joystick leftJoystick;
    private Map<MustangSubsystemBase, HealthState> healthRequirements = new HashMap<MustangSubsystemBase, HealthState>();


    public SingleJoystickDrive(TankDrive driveBase, Joystick leftJoystick) {
        super();
        this.driveBase = driveBase;
        this.leftJoystick = leftJoystick;
        addRequirements(driveBase);       
        healthRequirements.put(driveBase, HealthState.YELLOW);


    }

    // Called just before this Command runs the first time
    @Override
    public void execute() { 
        driveBase.arcadeDrive(-1 * leftJoystick.getY(), -1 * leftJoystick.getTwist(), true);
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return healthRequirements;
    }

}
