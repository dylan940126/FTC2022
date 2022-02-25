package org.firstinspires.ftc.teamcode.custommodules;

public class AbsolutePose {
    public double x, y, direction;

    public AbsolutePose(double x, double y, double direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public void add(RelativeVector vector) {
        direction += vector.turn;
        x += vector.right * Math.cos(direction) - vector.forward * Math.sin(direction);
        y += vector.right * Math.sin(direction) + vector.forward * Math.cos(direction);
    }

    public RelativeVector convertToVector() {
        return new RelativeVector(x * Math.cos(direction) + y * Math.sin(direction),
                -x * Math.sin(direction) + y * Math.cos(direction),
                direction
        );
    }

    public AbsolutePose clone() {
        return new AbsolutePose(x, y, direction);
    }
}
