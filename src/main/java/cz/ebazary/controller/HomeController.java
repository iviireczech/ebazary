package cz.ebazary.controller;

import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.Region;
import cz.ebazary.model.request.UserRequest;
import cz.ebazary.service.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(final Model model) {

        final UserRequest userRequest = new UserRequest();
        model.addAttribute("userRequest", userRequest);
        model.addAttribute("categories", Category.values());
        model.addAttribute("regions", Region.values());

        return "index";

    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    public String result(final @ModelAttribute("userRequest") UserRequest userRequest,
                         final Model model) {

        final List<ItemDTO> itemDTOs =
                itemService
                        .getItems(
                                null,
                                userRequest.getCategory(),
                                userRequest.getQuery()
                        );

        model.addAttribute("items", itemDTOs);

        return "result";

    }


}
