package cz.ebazary.model.bazaar.locality;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum District {
    AB("Praha"),
    BN("Benešov"),
    BE("Beroun"),
    BK("Blansko"),
    BM("Brno-město"),
    BI("Brno-venkov"),
    BR("Bruntál"),
    BV("Břeclav"),
    CL("Česká Lípa"),
    CB("České Budějovice"),
    CK("Český Krumlov"),
    DC("Děčín"),
    DO("Domažlice"),
    FM("Frýdek-Místek"),
    HB("Havlíčkův Brod"),
    HO("Hodonín"),
    HK("Hradec Králové"),
    CH("Cheb"),
    CV("Chomutov"),
    CR("Chrudim"),
    JN("Jablonec nad Nisou"),
    JE("Jeseník"),
    JC("Jičín"),
    JI("Jihlava"),
    JH("Jindřichův Hradec"),
    KV("Karlovy Vary"),
    KI("Karviná"),
    KD("Kladno"),
    KT("Klatovy"),
    KO("Kolín"),
    KM("Kroměříž"),
    KH("Kutná Hora"),
    LI("Liberec"),
    LT("Litoměřice"),
    LN("Louny"),
    ME("Mělník"),
    MB("Mladá Boleslav"),
    MO("Most"),
    NA("Náchod"),
    NJ("Nový Jičín"),
    NB("Nymburk"),
    OC("Olomouc"),
    OP("Opava"),
    OV("Ostrava"),
    PU("Pardubice"),
    PE("Pelhřimov"),
    PI("Písek"),
    PJ("Plzeň-jih"),
    PM("Plzeň-město"),
    PS("Plzeň-sever"),
    PY("Praha-východ"),
    PZ("Praha-západ"),
    PT("Prachatice"),
    PV("Prostějov"),
    PR("Přerov"),
    PB("Příbram"),
    RA("Rakovník"),
    RO("Rokycany"),
    RK("Rychnov nad Kněžnou"),
    SM("Semily"),
    SO("Sokolov"),
    ST("Strakonice"),
    SY("Svitavy"),
    SU("Šumperk"),
    TA("Tábor"),
    TC("Tachov"),
    TP("Teplice"),
    TU("Trutnov"),
    TR("Třebíč"),
    UH("Uherské Hradiště"),
    UL("Ústí nad Labem"),
    UO("Ústí nad Orlicí"),
    VS("Vsetín"),
    VY("Vyškov"),
    ZL("Zlín"),
    ZN("Znojmo"),
    ZR("Žďár nad Sázavou");

    private String name;

    District(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<District> findByName(final String name) {

        final String correctedName;
        if ("Brno".equals(name)) {
            correctedName = BM.getName();
        } else if (name.matches("Brno [^0-9]+")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if ("Plzeň".equals(name)) {
            correctedName = PM.getName();
        } else if (name.matches("Plzeň [^0-9]+")) {
            if (name.contains("-")) {
                correctedName = StringUtils.trimAllWhitespace(name);
            } else {
                correctedName = name.replaceAll(" ", "-");
            }
        } else if (name.matches("Praha \\d+")) {
            correctedName = AB.getName();
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
        } else {
            correctedName = name;
        }

        return Arrays
                .stream(District.values())
                .filter(district -> district.getName().equals(correctedName))
                .findAny();

    }

}
