package cn.momia.common.reload;

import java.util.Date;

public abstract class Reloadable {
    private Date lastReloadTime = null;
    private int reloadIntervalMinutes = 24 * 60;

    public void setReloadIntervalMinutes(int reloadIntervalMinutes) {
        this.reloadIntervalMinutes = reloadIntervalMinutes;
    }

    protected synchronized void reload() {
        if (!isOutOfDate()) return;

        try {
            doReload();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lastReloadTime = new Date();
        }
    }

    protected boolean isOutOfDate() {
        return lastReloadTime == null || lastReloadTime.before(new Date(new Date().getTime() - reloadIntervalMinutes * 60 * 1000));
    }

    protected abstract void doReload();
}
