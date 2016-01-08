package cn.momia.common.core.exception;

public class MomiaErrorException extends MomiaException {
    public MomiaErrorException(String msg) {
        super(msg);
    }

    public MomiaErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}
