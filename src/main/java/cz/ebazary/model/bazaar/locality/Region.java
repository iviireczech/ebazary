package cz.ebazary.model.bazaar.locality;

public enum Region {

    JC("Jihočeský kraj", District.CB, District.CK, District.JH, District.PI, District.PT, District.ST, District.TA),
    JM("Jihomoravský kraj", District.BK, District.BM, District.BI, District.BV, District.HO, District.VY, District.ZN),
    KA("Karlovarský kraj", District.CH, District.KV, District.SO),
    KR("Královéhradecký kraj", District.HK, District.JC, District.NA, District.RK, District.TU),
    LI("Liberecký kraj", District.CL, District.JN, District.LI, District.SM),
    MO("Moravskoslezský kraj", District.SM, District.BR, District.FM, District.KI, District.NJ, District.OP, District.OV),
    OL("Olomoucký kraj", District.JE, District.OC, District.PV, District.PR, District.SU),
    PA("Pardubický kraj", District.CR, District.PU, District.SY, District.UO),
    PL("Plzeňský kraj", District.DO, District.KT, District.PJ, District.PM, District.PS, District.RO, District.TC),
    PR("Hlavní město Praha", District.AB),
    ST("Středočeský kraj", District.BN, District.BE, District.KD, District.KO, District.KH, District.ME, District.MB, District.NB, District.PY, District.PZ, District.PB, District.RA),
    US("Ústecký kraj", District.DC, District.CV, District.LT, District.LN, District.MO, District.TP, District.UL),
    VY("Kraj Vysočina", District.HB, District.JI, District.PE, District.TR, District.ZR),
    ZL("Zlínský kraj", District.KM, District.UH, District.VS, District.ZL),
    CZ("Celá ČR", District.values());

    private String name;
    private District[] districts;

    Region(final String name, final District... districts) {
        this.name = name;
        this.districts = districts;
    }

    public String getName() {
        return name;
    }

    public District[] getDistricts() {
        return districts;
    }

}
