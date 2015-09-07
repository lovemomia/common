package cn.momia.common.api.exception;

public class MomiaFailedException extends MomiaException {
    public MomiaFailedException(String msg) {
        super(msg);
    }

    public MomiaFailedException(String msg, Throwable t) {
        super(msg, t);
    }
}
