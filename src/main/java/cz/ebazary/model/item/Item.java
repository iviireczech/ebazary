package cz.ebazary.model.item;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.bazaar.locality.Locality;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class Item {

    @NotNull
    private BazaarType bazaarType;

    @NotNull
    private Category category;

    @NotBlank
    private String url;

    @NotNull
    private LocalDate insertionDate;

    @NotBlank
    private String description;

    @Price
    private ItemPrice itemPrice;

    private String mainImageUrl;

    @NotNull
    private List<String> otherImagesUrl;

    @Locality
    private ItemLocality itemLocality;

    private String phoneNumber;

    private String email;

    public Item() {
        otherImagesUrl = new ArrayList<>();
    }

}
