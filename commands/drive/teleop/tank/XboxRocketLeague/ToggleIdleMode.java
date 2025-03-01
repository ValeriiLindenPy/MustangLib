package frc.team670.mustanglib.commands.drive.teleop.tank.XboxRocketLeague;

import java.util.Map;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.team670.mustanglib.commands.MustangCommand;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase;
import frc.team670.mustanglib.subsystems.MustangSubsystemBase.HealthState;
import frc.team670.mustanglib.utils.ConsoleLogger;
import frc.team670.mustanglib.subsystems.drivebase.DriveBase;


/**
 * The ToggleIdleMode class is a Java class that toggles the idle mode (the mode that the motors are set, to brake or coast) of a DriveBase object.
 * use case: H-drive. Allows you to use the H drive in coast and switch back to break to not be pushed around
 */
public class ToggleIdleMode extends InstantCommand implements MustangCommand{

    private DriveBase driveBase;

    public ToggleIdleMode(DriveBase driveBase) {
        super();
        ConsoleLogger.consoleLog("Switching idle mode");
        this.driveBase = driveBase;
    }

    
    /**
     * The initialize function toggles the idle mode of the drive base.
     */
    public void initialize() {
        driveBase.toggleIdleMode();
    }

    @Override
    public Map<MustangSubsystemBase, HealthState> getHealthRequirements() {
        return null;
    }


    
}
