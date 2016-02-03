package cz.ebazary.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.ebazary.model.bazaar.BazaarType;
import cz.ebazary.model.bazaar.category.Category;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.bazaar.locality.Locality;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
    @JsonIgnore
    private BazaarType bazaarType;

    @NotNull
    @Field(type = FieldType.String, store = true)
    private Category category;

    @NotNull
    @JsonIgnore
    private LocalDate insertionDate;

    @NotBlank
    @Field(type = FieldType.String, store = true)
    private String description;

    @Price
    @JsonIgnore
    private ItemPrice itemPrice;

    @JsonIgnore
    private String mainImageUrl;

    @NotNull
    @JsonIgnore
    private List<String> otherImagesUrl;

    @Locality
    @Field(type = FieldType.Object, store = true)
    private ItemLocality itemLocality;

    @JsonIgnore
    private String phoneNumber;

    @JsonIgnore
    private String email;

    public Item() {
        otherImagesUrl = new ArrayList<>();
    }

}
