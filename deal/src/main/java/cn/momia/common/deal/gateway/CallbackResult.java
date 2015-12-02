package cn.momia.common.deal.gateway;

public class CallbackResult {
    public static final CallbackResult SUCCESS = new CallbackResult(true);
    public static final CallbackResult FAILED = new CallbackResult(false);

    private boolean successful;

    public boolean isSuccessful() {
        return successful;
    }

    public CallbackResult(boolean successful) {
        this.successful = successful;
    }
}
