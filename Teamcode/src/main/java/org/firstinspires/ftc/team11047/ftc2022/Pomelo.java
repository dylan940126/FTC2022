package org.firstinspires.ftc.team11047.ftc2022;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Pomelo {

    public DcMotor lf = null;
    public DcMotor rf = null;
    public DcMotor rb = null;
    public DcMotor lb = null;
    public DcMotor suck = null;
    public DcMotor turn = null;
    public DcMotor angle = null;
    public DcMotor rail = null;

    public Servo basket = null;
    public Servo teammark = null;
    public Servo duck = null;

    DistanceSensor distance = null;
    RevBlinkinLedDriver led = null;
    public boolean bluseside = false;

    public ElapsedTime runtime = new ElapsedTime();
    IMU imu;

    private final double plane = 1;
    private final double tilt = 0.3;
    private final double SuckPower = 1;
    private final double fold = 0;

    Pomelo() {

    }

    public void init(HardwareMap ahwMap) {
        HardwareMap hardware = ahwMap;

        lf = hardware.get(DcMotor.class, "lf");
        rf = hardware.get(DcMotor.class, "rf");
        lb = hardware.get(DcMotor.class, "lb");
        rb = hardware.get(DcMotor.class, "rb");
        suck = hardware.get(DcMotor.class, "suck");
        turn = hardware.get(DcMotor.class, "turn");
        angle = hardware.get(DcMotor.class, "angle");
        rail = hardware.get(DcMotor.class, "rail");
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        rb.setDirection(DcMotorSimple.Direction.REVERSE);
        rail.setDirection(DcMotorSimple.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turn.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turn.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        angle.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rail.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTrainMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTrainMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        angle.setTargetPosition(0);
        rail.setTargetPosition(0);
        turn.setTargetPosition(0);
        angle.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rail.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        if (bluseside)
            turn.setDirection(DcMotorSimple.Direction.REVERSE);
        else
            turn.setDirection(DcMotorSimple.Direction.FORWARD);
        turn.setMode(DcMotor.RunMode.RUN_TO_POSITION);//0~4300 -> 0~180 degrees

        basket = hardware.get(Servo.class, "basket");
        teammark = hardware.get(Servo.class, "teammark");
        duck = hardware.get(Servo.class, "duck");

        distance = hardware.get(DistanceSensor.class, "dis");

        led = hardware.get(RevBlinkinLedDriver.class, "led");

        setBasket(false);
        setTeammark(fold);
        imu = new IMU();
        imu.init(hardware);
    }

    private double x = 0, y = 0, dx, dy, tlf, tlb, trf, trb, llf = 0, llb = 0, lrf = 0, lrb = 0, dlf, dlb, drf, drb;

    public void Odometry() {
        tlf = lf.getCurrentPosition();
        tlb = lb.getCurrentPosition();
        trf = rf.getCurrentPosition();
        trb = rb.getCurrentPosition();
        dlf = tlf - llf;
        dlb = tlb - llb;
        drf = trf - lrf;
        drb = trb - lrb;
        llf = tlf;
        llb = tlb;
        lrf = trf;
        lrb = trb;
        dy = (dlf + dlb - drf - drb) / 4 / 537.6 * 10 * Math.PI;
        y += dy;
    }

    public void resetEncoder() {
        setDriveTrainMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setDriveTrainMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        llf = llb = lrf = lrb = 0;
    }

    public double getY() {
        return y;
    }

    public void setY(double yy) {
        y = yy;
    }

    public double getX() {
        return x;
    }

    public void setX(double xx) {
        x = xx;
    }

    public void setXY(double xx, double yy) {
        setX(xx);
        setY(yy);
    }

    public void move(double x, double y, double r) {
        lf.setPower(x + y + r);
        lb.setPower(-x + y + r);
        rf.setPower(x - y + r);
        rb.setPower(-x - y + r);
    }

    public void setDriveTrainMode(DcMotor.RunMode mode) {
        lf.setMode(mode);
        lb.setMode(mode);
        rf.setMode(mode);
        rb.setMode(mode);
    }

    public void setBasket(boolean put) {
        if (put)
            basket.setPosition(tilt);
        else
            basket.setPosition(plane);
    }

    public void Suck(double p) {
        suck.setPower(SuckPower * p);
    }

    public void setAngle(int pos, double power) {
        angle.setTargetPosition(pos);
        angle.setPower(power);
    }

    public void setRail(int pos, double power) {
        rail.setTargetPosition(pos);
        rail.setPower(power);
    }

    public void setTurn(int pos, double power) {
        turn.setTargetPosition(pos);
        turn.setPower(power);
    }

    public void setTeammark(double put) {
        teammark.setPosition(put);
    }
}
