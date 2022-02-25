package org.firstinspires.ftc.teamcode.custommodules;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class TurnTable {
    private final LinearOpMode opMode;
    private final DcMotor raise, extend, intake, spinner;
    private final CRServo turn;
    private final Servo container, lock, ship;
    private final DistanceSensor object_detector;
    private boolean last_direction, last_detect = false;
    private double last_turn_time, detect_time = 0;

    public TurnTable(LinearOpMode opMode) {
        this.opMode = opMode;
        raise = opMode.hardwareMap.dcMotor.get("la");
        raise.setPower(-0.2);
        opMode.sleep(500);
        raise.setPower(0);
        raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend = opMode.hardwareMap.dcMotor.get("extend");
        extend.setDirection(DcMotor.Direction.REVERSE);
        extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake = opMode.hardwareMap.dcMotor.get("intake");
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        spinner = opMode.hardwareMap.dcMotor.get("spinner");
        ship = opMode.hardwareMap.servo.get("shipping");
        turn = opMode.hardwareMap.crservo.get("btm");
        lock = opMode.hardwareMap.servo.get("lock");
        container = opMode.hardwareMap.servo.get("lifter");
        setContainer(false);
        object_detector = opMode.hardwareMap.get(DistanceSensor.class, "objdetect");
        last_direction = false;
        last_turn_time = opMode.getRuntime();
    }

    public void setDirection(boolean left) {
        if (last_direction ^ left) {
            lock.setPosition(0.6);
            turn.setPower(left ? -1 : 1);
            last_direction = left;
            last_turn_time = opMode.getRuntime();
        } else if (opMode.getRuntime() - last_turn_time > 1.5) {
            lock.setPosition(0.47);
            turn.setPower(0);
        }
    }

    public void Collect() {
        intake.setPower(0.7);
    }

    public void backFlow() {
        intake.setPower(-0.8);
    }

    public void stopCollect() {
        intake.setPower(0);
    }

    public void setHeight(int height) {
        switch (height) {
            case 0:
                raise.setTargetPosition(0);
                break;
            case 1:
                raise.setTargetPosition(900);
                break;
            case 2:
                raise.setTargetPosition(1100);
                break;
        }
        raise.setPower(0.5);
        raise.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setExtendSpeed(double speed) {
        extend.setPower(0.4 * speed);
    }

    public void setContainer(boolean pour) {
        container.setPosition(pour ? 0.55 : 1);
    }

    public boolean isCarry() {
        boolean now_detect = object_detector.getDistance(DistanceUnit.CM) < 13;
        if (!last_detect && now_detect)
            detect_time = opMode.getRuntime();
        last_detect = now_detect;
        return now_detect && (opMode.getRuntime() - detect_time) > 0.5;
    }

    public void setSpinner(double power) {
        spinner.setPower(power);
    }
}