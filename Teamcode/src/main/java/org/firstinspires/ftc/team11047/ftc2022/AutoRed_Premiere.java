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
    public void runOpMode() throws InterruptedException {

        robot.bluseside = false;
        robot.init(hardwareMap);
        autoConstant = new Auto_Constant();
        distance = hardwareMap.get(DistanceSensor.class, "wall");
        tube = hardwareMap.get(DistanceSensor.class, "redtube");
        gyrosensor = hardwareMap.gyroSensor.get("gyro");
        gyro = (ModernRoboticsI2cGyro) gyrosensor;
        gyro.calibrate();
        while (gyro.isCalibrating()) {
            telemetry.addLine("Gyro Calibrating.");
            telemetry.update();
        }
        telemetry.addLine("Complete");
        telemetry.update();
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"), cameraMonitorViewId);
        pipeline = new DuckPipeline();
        webcam.setPipeline(pipeline);

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
        sleep(900);
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
                    default:
                        Default(i);
                        break;
                }
                robot.Suck(-1);
                sleep(200);

                if (robot.distance.getDistance(DistanceUnit.CM) < 15 && robot.runtime.seconds() < 27.5) {
                    back();
                    fold();
                }
            }
        }
        if (robot.getY() < autoConstant.warehouse_position)
            moveY(autoConstant.warehouse_position, 0);
        sleep(5000);
    }

    void turn(double deg) {
        int tar = (int) (deg / 180 * -4300);
        robot.setTurn(tar, 0.27);
    }

    void one() {
        moveY(autoConstant.warehouse_position, -2);
        suck(0);
    }

    void two() {
        moveY(autoConstant.warehouse_position + 20, -2);
        suck(10);
    }

    void three() {
        moveY(autoConstant.warehouse_position + 20, -2);
        suck(20);
    }

    void four() {
        moveY(autoConstant.warehouse_position, -2);
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1750) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading() - 35) * 0.025);
        }
        robot.move(0, 0, 0);
    }

    void five() {
        moveY(autoConstant.warehouse_position + 25, 0);
        timelapse = robot.runtime.milliseconds();
        suck(30);
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1500) {
            robot.Odometry();
            robot.move(0, 0.22, (getHeading()) * 0.025);
        }
        robot.move(0, 0, 0);
    }

    void Default(int i) {
        moveY(autoConstant.warehouse_position + 30, 0);
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 1250) {
            robot.Odometry();
            robot.move(0, 0.3, (getHeading() - (i % 3 + 1) * 10) * 0.025);
        }
        robot.move(0, 0, 0);
    }


    public void moveY(double y, int heading) {
        double ypower = 0.95;
        double slowpower = 0.25;
        double downpower;
        double sy = robot.getY();
        double dis = Math.abs(robot.getY() - y);
        double slowdis = dis * 0.6;
        while (dis > 3.5 && opModeIsActive()) {
            robot.Odometry();
            dis = Math.abs(robot.getY() - y);
            double slow = Math.abs(robot.getY() - sy) - slowdis;
            if (slow <= 1)
                downpower = ypower;
            else
                downpower = Range.clip(0.0000001 / (slow / Math.abs(y - slowdis)) + slowpower, slowpower, ypower);
            robot.move(0.15, downpower * (y - robot.getY() > 0 ? 1 : -1), Range.clip((getHeading() - heading) * 0.06, -0.25, 0.25));
            telemetry.addData("y", robot.getY());
            telemetry.update();
        }
        robot.move(0, 0, 0);
        robot.setY(y);
    }

    void suck(int heading) {
        timelapse = robot.runtime.milliseconds();
        while (opModeIsActive() && robot.distance.getDistance(DistanceUnit.CM) > 15 && robot.runtime.milliseconds() - timelapse < 2000) {
            robot.Odometry();
            robot.move(0, 0.19, (getHeading() - heading) * 0.025);
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
        int angle = autoConstant.top_angle;
        int rail = autoConstant.top_rail;
        robot.setAngle(angle, 0.4);
        robot.setRail(rail, 1);
        sleep(100);
        turn(autoConstant.turn_degree - 5);
    }

    void back() {
        //back to white strip
        while (opModeIsActive() && distance.getDistance(DistanceUnit.CM) > 12) {
            robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.YELLOW);
            double p = (distance.getDistance(DistanceUnit.CM) - 12) * 0.09 + 0.15;
            if (distance.getDistance(DistanceUnit.CM) < 25)
                robot.move(Range.clip(p, 0.2, 0.5), Range.clip(-1 + p, -0.8, -0.6), Range.clip(getHeading() * 0.04, -0.3, 0.3));
            else
                robot.move(0.8, -0.5, Range.clip(getHeading() * 0.04, -0.3, 0.3));
            telemetry.addLine("back to white strip");
            telemetry.update();
        }
        robot.move(0, 0, 0);
        stretch();
        //readjust
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE_VIOLET);
        while (opModeIsActive() && tube.getDistance(DistanceUnit.CM) > 6) {
            robot.Odometry();
            robot.move(0.25, -0.9, Range.clip((getHeading() - 7) * 0.04, -0.3, 0.3));
        }
        robot.move(0, 0, 0);
        robot.setY(115);
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.DARK_RED);
        //back to put
        while (opModeIsActive() && robot.getY() > autoConstant.start_position + 5) {
            robot.Odometry();
            robot.move(0.05, Range.clip(-1 * (robot.getY() - autoConstant.start_position - 15) / 35, -1, -0.2), getHeading() * 0.025);
            if (robot.getY() < autoConstant.start_position + 20) robot.setBasket(true);
            telemetry.addLine("back to put");
            telemetry.update();
        }
        robot.move(0, 0, 0);
        sleep(300);
        robot.setBasket(false);
        while (opModeIsActive() && Math.abs(getHeading()) > 0) {
            robot.Odometry();
            robot.move(0, 0, Range.clip(getHeading() * 0.5, -0.5, 0.5));
        }
        robot.led.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE_VIOLET);
    }

    public void fold() {
        turn(0);
        robot.setRail(0, 1);
        robot.setAngle(0, 0.2);
    }

    public void put() {
        robot.setBasket(true);
        sleep(500);
        robot.setBasket(false);
        sleep(150);
    }

    int getHeading() {
        int hd = gyro.getHeading();
        return hd > 180 ? (hd - 360) : hd;
    }
}
