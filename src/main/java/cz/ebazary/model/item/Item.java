package cz.ebazary.model.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(indexName = "item")
@Table(value = "item")
public class Item {

    @NotBlank
    @Id
    @PrimaryKey
    private String url;

    @NotNull
    @JsonIgnore
    private String bazaarType;

    @NotNull
    @Field(type = FieldType.String, store = true)
    private String category;

    @NotNull
    @JsonIgnore
    private Date insertionDate;

    @NotBlank
    @Field(type = FieldType.String, store = true)
    private String description;

    @JsonIgnore
    private BigDecimal price;

    @JsonIgnore
    private String currency;

    @JsonIgnore
    private boolean negotiatedPrice;

    @JsonIgnore
    private boolean priceInDescription;

    @JsonIgnore
    private String mainImageUrl;

    @NotNull
    @JsonIgnore
    private List<String> otherImagesUrl;

    @Field(type = FieldType.String, store = true)
    private String district;

    @Field(type = FieldType.String, store = true)
    private String region;

    @JsonIgnore
    private String phoneNumber;

    @JsonIgnore
    private String email;

    public Item() {
        otherImagesUrl = new ArrayList<>();
    }

}
