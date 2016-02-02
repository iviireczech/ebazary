package cz.ebazary.model.item;

import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.District;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(indexName = "item")
public class Item {

    @NotBlank
    @Id
    private String url;

    @NotNull
    private BazaarType bazaarType;

    @NotNull
    private Category category;

    @NotNull
    private LocalDate insertionDate;

    @NotBlank
    private String description;

    @Price
    private ItemPrice itemPrice;

    private String mainImageUrl;

    @NotNull
    private List<String> otherImagesUrl;

    @NotEmpty
    private List<District> districts;

    private String phoneNumber;

    private String email;

    public Item() {
        otherImagesUrl = new ArrayList<>();
        districts = new ArrayList<>();
    }

}
