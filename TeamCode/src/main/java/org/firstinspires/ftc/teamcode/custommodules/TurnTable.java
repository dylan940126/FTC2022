package org.firstinspires.ftc.teamcode.custommodules;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TurnTable {
    private final LinearOpMode opMode;
    private final DcMotor raise, extend, intake, spinner;
    private final Servo container, ship;
    private final DistanceSensor object_detector;
    private boolean last_direction, last_detect = false;
    private double detect_time = 0;

    public TurnTable(@NonNull LinearOpMode opMode) {
        this.opMode = opMode;
        container = opMode.hardwareMap.servo.get("lifter");
        noPour();
        intake = opMode.hardwareMap.dcMotor.get("intake");
        intake.setDirection(DcMotor.Direction.REVERSE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        turn = opMode.hardwareMap.dcMotor.get("btm");
//        turn.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        turn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        turn.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spinner = opMode.hardwareMap.dcMotor.get("spinner");
        ship = opMode.hardwareMap.servo.get("ship");
        ship.setDirection(Servo.Direction.REVERSE);
        ship.scaleRange(0, 0.94);
        object_detector = opMode.hardwareMap.get(DistanceSensor.class, "objdetect");
        last_direction = false;
        raise = opMode.hardwareMap.dcMotor.get("la");
        raise.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extend = opMode.hardwareMap.dcMotor.get("extend");
        extend.setDirection(DcMotor.Direction.REVERSE);
        extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        resetRaise();
    }

    public void shipPosition(double position) {
        ship.setPosition(position);
    }

    public void collect() {
        if (isCollectable())
            intake.setPower(0.8);
    }

    public void backFlow() {
        intake.setPower(-1);
    }

    public void stopCollect() {
        intake.setPower(0);
    }

    public void setHeight(int height) {
        switch (height) {
            case 0:
                extend.setTargetPosition(0);
                if (extend.getCurrentPosition() < 200)
                    raise.setTargetPosition(250);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 1:
                raise.setTargetPosition(450);
                extend.setTargetPosition(1690);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 2:
                raise.setTargetPosition(780);
                extend.setTargetPosition(1790);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 3:
                raise.setTargetPosition(1050);
                extend.setTargetPosition(1900);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 4:
                raise.setTargetPosition(500);
                break;
            default:
                break;
        }
        raise.setPower(1);
        raise.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setExtendSpeed(double power) {
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (power < 0 || extend.getCurrentPosition() < 1950)
            extend.setPower(power);
        else
            extend.setPower(0);
    }

    public void pour() {
        container.setPosition(0.3);
    }

    public void noPour() {
        container.setPosition(1);
    }

    public boolean isCarry() {
        opMode.telemetry.addData("dist", object_detector.getDistance(DistanceUnit.CM));
        if (!isCollectable())
            return last_detect;
        boolean now_detect = object_detector.getDistance(DistanceUnit.CM) < 15;
        if (!last_detect && now_detect)
            detect_time = opMode.getRuntime();
        last_detect = now_detect;
        return now_detect && (opMode.getRuntime() - detect_time) > 0.15;
    }

    public boolean isCollectable() {
        return raise.getCurrentPosition() < 300 && extend.getCurrentPosition() < 100;
    }

    public boolean isPourable() {
        return extend.getCurrentPosition() > 1400;
    }

    public void setSpinner(double power) {
        spinner.setPower(power);
    }

    public void resetRaise() {
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        raise.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extend.setPower(-0.5);
        raise.setPower(-0.5);
        opMode.sleep(1000);
        extend.setPower(0);
        raise.setPower(0);
        opMode.sleep(200);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setHeight(0);
    }
}