package cz.ebazary.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDTO {

    private String bazaarName;
    private String category;
    private String url;
    private String insertionDate;
    private String description;
    private String itemPrice;
    private String mainImageUrl;
    private List<String> otherImagesUrl;
    private String itemLocality;
    private String phoneNumber;
    private String email;
    public ItemDTO() {
        otherImagesUrl = new ArrayList<>();
    }

}
