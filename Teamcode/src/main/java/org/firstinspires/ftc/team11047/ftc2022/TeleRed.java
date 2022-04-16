package org.firstinspires.ftc.team11047.ftc2022;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp
public class TeleRed extends OpMode {

    Pomelo robot = new Pomelo();
    boolean ifsuck = false;
    boolean lastifsuck = false;
    boolean ifturn = false;
    boolean lastifturn = false;
    boolean lastteammarkup = false;
    boolean lastteammarkdown = false;
    boolean ifreset = false;
    boolean lastifreset = false;
    boolean ifa = false;
    boolean lastifa = false;
    boolean ify = false;
    boolean lastify = false;
    boolean tmp = false;
    boolean railreset = true;
    boolean anglereset = true;
    boolean angleholdMode = false;
    boolean railholdMode = false;
    boolean resetMode = false;
    double timelapse = 0;
    double teammarkpos = 0;

    @Override
    public void init() {
        robot.bluseside = false;
        robot.init(hardwareMap);
    }

    @Override
    public void loop() {
        //chassis
        double x = gamepad1.left_stick_x * 0.8;
        double y = gamepad1.left_stick_y * -1;
        double r = gamepad1.right_stick_x * 0.5;
        robot.move(x, y, r);

        //suck
        tmp = gamepad1.right_bumper;
        if (tmp && !lastifsuck)
            ifsuck = !ifsuck;
        lastifsuck = tmp;
        if (gamepad1.left_bumper || robot.distance.getDistance(DistanceUnit.CM) < 10)
            robot.Suck(-1);
        else if (ifsuck)
            robot.Suck(0.75);
        else
            robot.Suck(0);
        if (robot.distance.getDistance(DistanceUnit.CM) < 10)
            robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.GREEN);
        else
            robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.LIGHT_CHASE_RED);
        //basket
        robot.setBasket(gamepad2.x);

        //duck
        if (gamepad1.a)
            robot.duck.setPosition(0);
        else
            robot.duck.setPosition(0.5);

        //turn
        int target;
        tmp = gamepad1.y;
        if (tmp && !lastifturn)
            ifturn = !ifturn;
        lastifturn = tmp;
        if (ifturn) {
            target = 4300;
            // 180 degrees = 4300 ticks
        } else {
            target = 0;
        }
        robot.setTurn(target, 0.6);

        //teammark
        tmp = gamepad2.dpad_up;
        if (tmp && !lastteammarkup)
            teammarkpos -= (teammarkpos > 0.7 ? 0.05 : 0.1);
        lastteammarkup = tmp;
        tmp = gamepad2.dpad_down;
        if (tmp && !lastteammarkdown)
            teammarkpos += (teammarkpos >= 0.7 ? 0.05 : 0.1);
        lastteammarkdown = tmp;
        teammarkpos = Range.clip(teammarkpos, 0, 1);
        robot.setTeammark(teammarkpos);

        //resetangle
        tmp = gamepad1.x;
        if (tmp && !lastifreset)
            ifreset = !ifreset;
        lastifreset = tmp;
        resetMode = ifreset;

        if (resetMode) {
            if (gamepad2.a) {
                if (anglereset)
                    robot.angle.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.angle.setPower(-0.35);
                anglereset = false;
            } else if (gamepad2.y) {
                if (anglereset)
                    robot.angle.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.angle.setPower(0.55);
                anglereset = false;
            } else {
                if (!anglereset) {
                    robot.angle.setPower(0);
                    robot.angle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    robot.angle.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                anglereset = true;
            }
            if (gamepad2.b) {
                if (railreset)
                    robot.rail.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.rail.setPower(-0.7);
                railreset = false;
            } else {
                if (!railreset) {
                    robot.rail.setPower(0);
                    robot.rail.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    robot.rail.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                }
                railreset = true;
            }
        } else {
            if (gamepad2.left_bumper || gamepad2.right_bumper)
                railholdMode = false;
            if (gamepad2.left_trigger > 0.5 || gamepad2.right_trigger > 0.5 || gamepad2.b)
                railholdMode = true;
            if (gamepad2.left_bumper) {
                robot.rail.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.setAngle(550, 0.4);
                robot.setRail(1950, 0.8);
            }
            if (gamepad2.right_bumper) {
                robot.rail.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.setRail(0, 0.8);
                if (robot.rail.getCurrentPosition() < 1800)
                    robot.setAngle(0, 0.2);
            }
            if (gamepad2.left_trigger > 0.5) {
                robot.rail.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.setAngle(50, 0.4);
                robot.rail.setPower(0.7);
            } else if (gamepad2.right_trigger > 0.5) {
                robot.rail.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.rail.setPower(-0.7);
                robot.setAngle(0, 0.2);
            } else if (gamepad2.b) {
                robot.rail.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                robot.rail.setPower(-0.4);
            } else if (railholdMode) {
                robot.rail.setPower(0);
            }

            int ang = 0;
            tmp = gamepad2.a;
            if (tmp && !lastifa)
                ang -= 50;
            lastifa = tmp;
            tmp = gamepad2.y;
            if (tmp && !lastify)
                ang += 50;
            lastify = tmp;
            robot.angle.setTargetPosition(robot.angle.getTargetPosition() + ang);
            robot.angle.setPower(0.35);
        }

        if (ifreset)
            telemetry.addLine("Mode: Reset.");
        else
            telemetry.addLine("Mode: Normal.");
        telemetry.addData("dis", robot.distance.getDistance(DistanceUnit.CM));
        telemetry.update();
    }
}