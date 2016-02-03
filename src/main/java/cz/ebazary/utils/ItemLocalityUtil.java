package cz.ebazary.utils;

import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.ItemLocality;
import cz.ebazary.model.bazaar.locality.Region;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public final class ItemLocalityUtil {

    private ItemLocalityUtil() {
    }

    public static Optional<ItemLocality> getItemLocality(final String localityString) {

        final Optional<Region> regionOptional = findRegionByName(localityString);
        if (regionOptional.isPresent()) {
            final ItemLocality itemLocality = new ItemLocality();
            itemLocality.setRegion(regionOptional.get());
            return Optional.of(itemLocality);
        } else {
            final Optional<District> districtOptional = findDistrictByName(localityString);
            if (districtOptional.isPresent()) {
                final ItemLocality itemLocality = new ItemLocality();
                itemLocality.setDistrict(districtOptional.get());
                return Optional.of(itemLocality);
            } else {
                return Optional.empty();
            }
        }

    }

    private static Optional<Region> findRegionByName(final String name) {

        return Arrays
                .stream(Region.values())
                .filter(region -> region.getName().equals(name))
                .findAny();

    }

    private static Optional<District> findDistrictByName(final String name) {

        final String correctedName;
        if ("Brno".equals(name)) {
            correctedName = District.BM.getName();
        } else if (name.matches("Brno [^0-9]+")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if ("Plzeň".equals(name)) {
            correctedName = District.PM.getName();
        } else if (name.matches("Plzeň [^0-9]+")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if (name.matches("Praha .*")) {
            correctedName = District.AB.getName();
        } else if (name.matches("Praha [^0-9]+")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if (name.matches("Frýdek.*Místek")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if (name.matches("Ostrava.*")){
            correctedName = District.OV.getName();
        } else {
            correctedName = name;
        }

        return Arrays
                .stream(District.values())
                .filter(district -> district.getName().equals(correctedName))
                .findAny();

    }

}
