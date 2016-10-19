package http2xp.web;

import org.eclipse.jetty.server.Dispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class Http2Controller {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok()
                .body("Welcome to http2");
    }

}
