package cz.ebazary.controller;

import cz.ebazary.model.item.Item;
import cz.ebazary.model.request.UserRequest;
import cz.ebazary.service.item.loaders.Loadable;
import cz.ebazary.service.item.loaders.SBazarItemLoader;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

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

        final Loadable loadable = new SBazarItemLoader();
        final List<Item> items = loadable.loadItems(LocalDate.now().minusDays(1));

        model.addAttribute("items", items);

        return "result";

    }


}
