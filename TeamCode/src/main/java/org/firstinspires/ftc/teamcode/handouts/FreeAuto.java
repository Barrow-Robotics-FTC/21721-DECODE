package org.firstinspires.ftc.robotcontroller.external.samples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


/* hello, my name is Mikey and im head of software for team 21721 :D
* If you are reading this it means that I gave you our sample autonomous, so here is a basic rundown:
* At the start of the match, you earn 3 extra points for leaving a launch area at the beginning of autonomous. This doesnt seem like alot, but
* it helps push our alliance to getting the movement ranked point, one of my team's first meet goals.
*/


@Autonomous(name="simple forward auto", group="Autonomous")
public class FreeAuto extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor         leftDrive   = null;
    private DcMotor         rightDrive  = null;

    @Override
    public void runOpMode() {

        // change these to your motor names
        leftDrive  = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        //make these directions match the way your robot is built
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();


        // Drive forward for 1 seconds at half motor power
        leftDrive.setPower(.5);
        rightDrive.setPower(.5);
        
        sleep(1000);

        // stop
        leftDrive.setPower(0);
        rightDrive.setPower(0);

    }
}
