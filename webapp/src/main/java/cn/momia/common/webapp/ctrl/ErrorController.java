package cn.momia.common.webapp.ctrl;

import cn.momia.common.core.http.MomiaHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController extends BaseController {
    @RequestMapping(value = "/400")
    public MomiaHttpResponse badRequest() {
        return MomiaHttpResponse.BAD_REQUEST;
    }

    @RequestMapping(value = "/403")
    public MomiaHttpResponse forbidden() {
        return MomiaHttpResponse.FORBIDDEN;
    }

    @RequestMapping(value = "/404")
    public MomiaHttpResponse notFound() {
        return MomiaHttpResponse.NOT_FOUND;
    }

    @RequestMapping(value = "/405")
    public MomiaHttpResponse methodNotAllowed() {
        return MomiaHttpResponse.METHOD_NOT_ALLOWED;
    }

    @RequestMapping(value = "/500")
    public MomiaHttpResponse internalServerError() {
        return MomiaHttpResponse.INTERNAL_SERVER_ERROR;
    }
}
