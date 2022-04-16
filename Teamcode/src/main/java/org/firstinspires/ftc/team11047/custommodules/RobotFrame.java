package org.firstinspires.ftc.team11047.custommodules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Config
public abstract class RobotFrame extends Chassis {
    public DcMotor raise, extend, intake, turn;
    public Servo container, ship;
    public CRServo spinner;
    public DistanceSensor object_detector;
    private boolean last_detect = false;
    private double detect_time = 0;
    public int zero_position = 0;
    public double duck_power = 0.7;

    public void initFrame() {
        initChassis();
        container = hardwareMap.servo.get("lifter");
        noPour();
        intake = hardwareMap.dcMotor.get("intake");
//        intake.setDirection(DcMotor.Direction.REVERSE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        turn = hardwareMap.dcMotor.get("btm");
        turn.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turn.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spinner = hardwareMap.crservo.get("spinner");
        ship = hardwareMap.servo.get("ship");
//        ship.setDirection(Servo.Direction.REVERSE);
        ship.scaleRange(0, 0.94);
        object_detector = hardwareMap.get(DistanceSensor.class, "objdetect");
        raise = hardwareMap.dcMotor.get("la");
        raise.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        extend = hardwareMap.dcMotor.get("extend");
        extend.setDirection(DcMotor.Direction.REVERSE);
        extend.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        resetRaise();
    }

    public void setTurn(double power) {
        turn.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turn.setPower(power);
    }

    public void turnTo(int direct) {
        turn.setTargetPosition(direct);
        turn.setPower(0.5);
        turn.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void shipPosition(double position) {
        ship.setPosition(position);
    }

    public void collect() {
        if (isCollectable())
            intake.setPower(0.8);
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
                extend.setTargetPosition(0);
                if (extend.getCurrentPosition() < 200)
                    raise.setTargetPosition(zero_position);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 1:
                raise.setTargetPosition(zero_position + 60);
                extend.setTargetPosition(1690);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 2:
                raise.setTargetPosition(zero_position + 330);
                extend.setTargetPosition(1790);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 3:
                raise.setTargetPosition(zero_position + 600);
                extend.setTargetPosition(1900);
                extend.setPower(1);
                extend.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                break;
            case 4:
                raise.setTargetPosition(zero_position + 600);
                break;
            default:
                break;
        }
        raise.setPower(1);
        raise.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setExtendSpeed(double power) {
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if (power < 0 || extend.getCurrentPosition() < 2050)
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
        if (!isCollectable())
            return false;
        boolean now_detect = object_detector.getDistance(DistanceUnit.CM) < 15;
        if (!last_detect && now_detect)
            detect_time = getRuntime();
        last_detect = now_detect;
        return now_detect && (getRuntime() - detect_time) > 0.15;
    }

    public boolean isCollectable() {
        return raise.getCurrentPosition() < 300 && extend.getCurrentPosition() < 100;
    }

    public boolean isPourable() {
        return extend.getCurrentPosition() > 1400;
    }

    public void setSpinnerRed() {
        spinner.setPower(-duck_power);
    }

    public void setSpinnerBlue() {
        spinner.setPower(duck_power);
    }

    public void setSpinnerStop() {
        spinner.setPower(0);
    }

    public void resetRaise() {
        extend.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        raise.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        extend.setPower(-0.5);
        raise.setPower(-0.3);
        sleep(1000);
        extend.setPower(0);
        raise.setPower(0);
        sleep(200);
        extend.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        raise.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setHeight(0);
    }
}