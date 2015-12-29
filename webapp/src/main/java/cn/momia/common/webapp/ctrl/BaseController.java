package cn.momia.common.webapp.ctrl;

import cn.momia.common.core.exception.MomiaException;
import cn.momia.common.core.exception.MomiaLoginException;
import cn.momia.common.core.exception.MomiaErrorException;
import cn.momia.common.core.http.MomiaHttpResponse;
import cn.momia.common.webapp.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected boolean isInvalidLimit(int start, int count) {
        int maxPage = Configuration.getInt("Limit.MaxPage");
        int maxPageSize = Configuration.getInt("Limit.MaxPageSize");

        return start < 0 || count <= 0 || start > maxPage * maxPageSize || count > maxPageSize;
    }

    protected Map<String, String> extractParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<String, String>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] values = entry.getValue();
            if (values.length <= 0) continue;
            params.put(entry.getKey(), entry.getValue()[0]);
        }

        return params;
    }

    @ExceptionHandler
    public MomiaHttpResponse exception(Exception exception) throws Exception {
        if (exception instanceof MomiaException) LOGGER.error("exception!!", exception);

        if (exception instanceof MomiaErrorException) {
            return MomiaHttpResponse.FAILED(exception.getMessage());
        } else if (exception instanceof MomiaLoginException) {
            return MomiaHttpResponse.TOKEN_EXPIRED;
        } else {
            throw exception;
        }
    }
}
