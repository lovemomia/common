package cn.momia.common.api.exception;

public abstract class MomiaException extends RuntimeException {
    public MomiaException() {}

    public MomiaException(String msg) {
        super(msg);
    }

    public MomiaException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
