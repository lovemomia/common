package cn.momia.common.deal.gateway;

import cn.momia.common.collection.MapWrapper;

public abstract class PrepayResult extends MapWrapper {
    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
