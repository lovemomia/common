package cn.momia.common.webapp.ctrl;

import cn.momia.common.api.exception.MomiaException;
import cn.momia.common.api.exception.MomiaExpiredException;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler
    public MomiaHttpResponse exception(Exception exception) throws Exception {
        if (exception instanceof MomiaException) LOGGER.error("exception!!", exception);

        if (exception instanceof MomiaFailedException) {
            return MomiaHttpResponse.FAILED(exception.getMessage());
        } else if (exception instanceof MomiaExpiredException) {
            return MomiaHttpResponse.TOKEN_EXPIRED;
        } else {
            throw exception;
        }
    }
}
