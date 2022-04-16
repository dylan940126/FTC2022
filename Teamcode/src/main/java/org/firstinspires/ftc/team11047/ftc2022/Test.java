package org.firstinspires.ftc.team11047.ftc2022;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//@Disabled
@TeleOp
public class Test extends OpMode {
    DcMotor turn = null;
    boolean ifturn = false;
    boolean lastifturn = false;
    boolean tmp = false;
    ModernRoboticsI2cGyro gyro = null;
    GyroSensor gyrosensor = null;
    ColorSensor color = null;
    DistanceSensor distance = null;
    RevBlinkinLedDriver led = null;

    @Override
    public void init() {
        turn = hardwareMap.get(DcMotor.class, "turn");
        color = hardwareMap.get(ColorSensor.class, "color");
        distance = hardwareMap.get(DistanceSensor.class, "bluetube");
        led = hardwareMap.get(RevBlinkinLedDriver.class, "led");
        gyrosensor = hardwareMap.gyroSensor.get("gyro");
        gyro = (ModernRoboticsI2cGyro) gyrosensor;
        gyro.calibrate();

        while (gyro.isCalibrating()) {
            telemetry.addLine("Calibrating.");
            telemetry.update();
        }
        turn.setDirection(DcMotorSimple.Direction.REVERSE);
        turn.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turn.setTargetPosition(0);
        turn.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        telemetry.addLine("It's good to go.");
        telemetry.update();
    }

    @Override
    public void loop() {
        led.setPattern(RevBlinkinLedDriver.BlinkinPattern.RAINBOW_RAINBOW_PALETTE);
        int target = 0;
        tmp = gamepad1.a;
        if (tmp && !lastifturn)
            ifturn = !ifturn;
        lastifturn = tmp;
        if (ifturn) {
            target = 4300;
            // 180 degrees = 4300 ticks
        } else {
            target = 0;
        }
        turn.setTargetPosition(target);
        if (Math.abs(turn.getCurrentPosition() - target) < 1400)
            turn.setPower(0.2);
        else
            turn.setPower(0.7);
        telemetry.addData("wall", distance.getDistance(DistanceUnit.CM));
        telemetry.addData("gyro", gyro.getHeading());
        telemetry.addData("pos", turn.getCurrentPosition());
        telemetry.addData("tar", turn.getTargetPosition());
        telemetry.update();
    }
}
