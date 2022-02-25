package org.firstinspires.ftc.teamcode.custommodules;

public class TwoSwitchs {
    private Switch buttonUp, buttonDown;
    private int level, total;

    public TwoSwitchs(int total_levels, int start_level) {
        total = total_levels;
        this.level = start_level;
        buttonUp = new Switch(false);
        buttonDown = new Switch(false);
    }

    public int getLevel() {
        return level;
    }

    public void refresh(boolean up, boolean down) {
        buttonUp.refresh(up);
        buttonDown.refresh(down);
        if (buttonUp.isJustPress())
            ++level;
        if (buttonDown.isJustPress())
            --level;
        if (level >= total)
            level = total - 1;
        else if (level < 0)
            level = 0;
    }
}