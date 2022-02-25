package org.firstinspires.ftc.teamcode.custommodules;

public class RelativeVector {
    public double right, forward, turn;

    public RelativeVector(double right, double forward, double turn) {
        this.right = right;
        this.forward = forward;
        this.turn = turn;
        while (this.turn > Math.PI)
            this.turn -= 2 * Math.PI;
        while (this.turn < -Math.PI)
            this.turn += 2 * Math.PI;
    }

    public RelativeVector to(RelativeVector target) {
        return new RelativeVector(target.right - right, target.forward - forward, target.turn - turn);
    }

    public void multiple(double times) {
        right *= times;
        forward *= times;
        turn *= times;
    }

    public double distance() {
        return Math.sqrt(right * right + forward * forward + turn * turn / Chassis.turn_weight / Chassis.turn_weight);
    }

    public void compressToDistance(double distance) {
        if (right != 0 || forward != 0 || turn != 0)
            multiple(distance / distance());
    }

    public void add(RelativeVector vector) {
        right += vector.right;
        forward += vector.forward;
        turn += vector.turn;
        while (turn > Math.PI)
            turn -= 2 * Math.PI;
        while (turn < -Math.PI)
            turn += 2 * Math.PI;
    }

    public RelativeVector clone() {
        return new RelativeVector(right, forward, turn);
    }
}
