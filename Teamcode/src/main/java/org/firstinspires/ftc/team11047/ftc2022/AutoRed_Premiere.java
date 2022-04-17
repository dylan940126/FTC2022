package org.firstinspires.ftc.team11047.ftc2022;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team11047.custommodules.MyMath;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

/* Please look at the file "How to modify" and follow the tips. */

@Autonomous
public class AutoRed_Premiere extends LinearOpMode {
    Pomelo robot = new Pomelo();
    OpenCvCamera webcam;
    DuckPipeline pipeline;
    double timelapse = 0;
    ModernRoboticsI2cGyro gyro = null;
    GyroSensor gyrosensor = null;
    Auto_Constant autoConstant;
    DistanceSensor distance = null;
    DistanceSensor tube = null;

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);
        autoConstant = new Auto_Constant();
        distance = hardwareMap.get(DistanceSensor.class, "wall");
        tube = hardwareMap.get(DistanceSensor.class, (robot.bluseside ? "bluetube" : "redtube"));
        gyrosensor = hardwareMap.gyroSensor.get("gyro");
        gyro = (ModernRoboticsI2cGyro) gyrosensor;
        if (!gyro.isCalibrating())
            gyro.calibrate();
        while (gyro.isCalibrating()) {
            telemetry.addLine("Gyro Calibrating.");
            telemetry.update();
        }
        telemetry.addLine("Complete");
        telemetry.update();
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"));
        pipeline = new DuckPipeline();
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                telemetry.addLine("Camera opened.");
                telemetry.update();
                webcam.setPipeline(pipeline);
                webcam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine("Camera failed.");
                telemetry.update();
            }
        });
        waitForStart();
        robot.runtime.reset();
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE_VIOLET);
        int state = pipeline.position;
        robot.setXY(0, autoConstant.start_position);//start position
        stretch(state);
        sleep(800);
        put();
        fold();
        int i;
        for (i = 1; i <= 10; i++) {
            if (robot.runtime.seconds() < 26) {
                robot.Suck(0.85);
                switch (i) {
                    case 1:
                        one();
                        break;
                    case 2:
                        two();
                        break;
                    case 3:
                        three();
                        break;
                    case 4:
                        four();
                        break;
                    case 5:
                        five();
                        break;
                    case 6:
                        six();
                        break;
                    default:
                        Default(i);
                        break;
                }
                robot.Suck(-1);
                if (robot.distance.getDistance(DistanceUnit.CM) < 15 && robot.runtime.seconds() < 27.5) {
                    back();
                    fold();
                }
            }
        }
        moveY(autoConstant.warehouse_position + 15);
        sleep(5000);
    }

    void turn(double deg) {
        int tar = (int) (deg / 180 * -4300);
        robot.setTurn(tar, 0.5);
    }

    void one() {
        moveY(autoConstant.warehouse_position);
        suck(0);
    }

    void two() {
        moveY(autoConstant.warehouse_position + 20);
        suck(10);
    }

    void three() {
        moveY(autoConstant.warehouse_position + 20);
        suck(20);
    }

    void four() {
        moveY(autoConstant.warehouse_position);
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1750) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading() - (robot.bluseside ? -35 : 35)) * 0.025);
        }
        robot.move(0, 0, 0);
    }

    void five() {
        moveY(autoConstant.warehouse_position + 25);
        timelapse = robot.runtime.milliseconds();
        suck(30);
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1500) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading()) * 0.025);
        }
        robot.move(0, 0, 0);
    }

    void six() {
        moveY(autoConstant.warehouse_position + 30);
        timelapse = robot.runtime.milliseconds();
        suck(30);
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1750) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading()) * 0.025);
        }
        robot.move(0, 0, 0);
    }

    void Default(int i) {
        moveY(autoConstant.warehouse_position + 30);
        suck(10 * (i % 2) + 10);
    }


    public void moveY(double y) {
        double ypower = 0.95;
        double dis = Math.abs(robot.getY() - y);
        while (dis > 3.5 && opModeIsActive()) {
            robot.Odometry();
            dis = Math.abs(robot.getY() - y);
            robot.move(0.15 * (robot.bluseside ? -1 : 1), -MyMath.distanceToPower((robot.getY() - y) / 50) * ypower, (getHeading() - (robot.bluseside ? 7 : -7)) * 0.07);
            telemetry.addData("y", robot.getY());
            telemetry.update();
        }
        robot.move(0, 0, 0);
        robot.setY(y);
    }

    void suck(int heading) {
        if (robot.bluseside)
            heading *= -1;
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.runtime.milliseconds() - timelapse < 50)
            robot.move(0, 0, 0);
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1750) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading() - heading) * 0.025);
        }
        robot.move(0, 0, 0);

    }

    public void stretch(int state) {
        int angle = 0;
        int rail = 0;
        switch (state) {
            case 0:
                angle = autoConstant.bottom_angle;
                rail = autoConstant.bottom_rail;
                break;
            case 1:
                angle = autoConstant.middle_angle;
                rail = autoConstant.middle_rail;
                break;
            case 2:
                angle = autoConstant.top_angle;
                rail = autoConstant.top_rail + 100;
                break;
        }
        robot.setAngle(angle, 0.4);
        robot.setRail(rail, 1);
        sleep(100);
        turn(autoConstant.turn_degree);
    }

    public void stretch() {
        new Thread(() -> {
            int angle = autoConstant.top_angle;
            int rail = autoConstant.top_rail;
            turn(autoConstant.turn_degree - 5);
            robot.setAngle(angle, 0.7);
            sleep(50);
            robot.setRail(rail, 1);
        }).start();
    }

    void back() {
        //back to white strip
        if (robot.distance.getDistance(DistanceUnit.CM) < 15)
            robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
        while (opModeIsActive() && distance.getDistance(DistanceUnit.CM) > 12) {
            robot.move(robot.bluseside ? -0.6 : 0.6, -0.4, Range.clip((getHeading() - (robot.bluseside ? -7 : 7)) * 0.04, -0.3, 0.3));
            telemetry.addLine("back to white strip");
            telemetry.update();
        }
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 500)
            robot.move(robot.bluseside ? -1 : 1, 0, 0);
        if (robot.distance.getDistance(DistanceUnit.CM) > 15)
            return;
        stretch();
        //readjust
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE_VIOLET);
        while (opModeIsActive() && (robot.getY() > autoConstant.warehouse_position || tube.getDistance(DistanceUnit.CM) > 6)) {
            robot.Odometry();
            robot.move(robot.bluseside ? -0.2 : 0.2, -0.9, Range.clip((getHeading() - (robot.bluseside ? -7 : 7)) * 0.04, -0.3, 0.3));
        }
        robot.move(0, 0, 0);
        robot.setY(122);
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_RED);
        //back to put
        double time = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.runtime.milliseconds() - time < 500) {
            robot.Odometry();
            robot.move(robot.bluseside ? -0.05 : 0.05, MyMath.distanceToPower((autoConstant.start_position - robot.getY()) / 100), getHeading() * 0.015);
            if (robot.getY() < autoConstant.start_position + 15) robot.setBasket(true);
            else time = robot.runtime.milliseconds();
            telemetry.addLine("back to put");
            telemetry.update();
        }
        robot.setBasket(false);
        while (opModeIsActive() && Math.abs(getHeading()) > 0) {
            robot.Odometry();
            robot.move(0, 0, Range.clip(getHeading() * 0.5, -0.5, 0.5));
        }
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE_VIOLET);
    }

    public void fold() {
        new Thread(() -> {
            while (opModeIsActive() && robot.rail.getCurrentPosition() > 500)
                robot.setRail(0, 1);
            robot.setAngle(0, 1);
            turn(0);
        }).start();
    }

    public void put() {
        robot.setBasket(true);
        sleep(500);
        robot.setBasket(false);
    }

    int getHeading() {
        int hd = gyro.getHeading();
        return hd > 180 ? (hd - 360) : hd;
    }
}
