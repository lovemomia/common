package cn.momia.common.api.exception;

public class MomiaErrorException extends MomiaException {
    public MomiaErrorException(String msg) {
        super(msg);
    }

    public MomiaErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}
