package cz.ebazary.controller;

import cz.ebazary.dto.ItemDTO;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.Region;
import cz.ebazary.model.request.UserRequest;
import cz.ebazary.service.item.loaders.Loadable;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private List<Loadable> loadables;

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

        final List<ItemDTO> itemDTOs = new ArrayList<>();

        loadables
                .stream()
                .forEach(
                    loadable -> itemDTOs.addAll(loadable.loadItems(LocalDate.now().minusDays(1)))
                );

        model.addAttribute("items", itemDTOs);

        return "result";

    }


}
