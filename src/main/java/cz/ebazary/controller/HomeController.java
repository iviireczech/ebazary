package cz.ebazary.controller;

import cz.ebazary.model.request.UserRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {

        final UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);

        return "index";

    }

    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public String result(final @ModelAttribute("userRequest") UserRequest userRequest,
                         final Model model) {

        model.addAttribute(userRequest.getQuery());

        return "result";

    }


}
