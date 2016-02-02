package cz.ebazary.utils;

import cz.ebazary.model.bazaar.locality.District;
import cz.ebazary.model.bazaar.locality.Region;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class DisctrictUtil {

    private DisctrictUtil() {
    }

    public static List<District> getDistricts(final String localityString) {

        final List<District> districts = new ArrayList<>();

        final Optional<Region> regionOptional = findRegionByName(localityString);
        if (regionOptional.isPresent()) {
            districts.addAll(Arrays.asList(regionOptional.get().getDistricts()));
        } else {
            final Optional<District> districtOptional = findDistrictByName(localityString);
            if (districtOptional.isPresent()) {
                districts.add(districtOptional.get());
            }
        }

        return districts;

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
