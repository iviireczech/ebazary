package cz.ebazary.model.bazaar.locality;

public enum Region {

    JC(District.CB, District.CK, District.JH, District.PI, District.PT, District.ST, District.TA),
    JM(District.BK, District.BM, District.BI, District.BV, District.HO, District.VY, District.ZN),
    KA(District.CH, District.KV, District.SO),
    KR(District.HK, District.JC, District.NA, District.RK, District.TU),
    LI(District.CL, District.JN, District.LI, District.SM),
    MO(District.SM, District.BR, District.FM, District.KI, District.NJ, District.OP, District.OV),
    OL(District.JE, District.OC, District.PV, District.PR, District.SU),
    PA(District.CR, District.PU, District.SY, District.UO),
    PL(District.DO, District.KT, District.PJ, District.PM, District.PS, District.RO, District.TC),
    PR(District.AB),
    ST(District.BN, District.BE, District.KD, District.KO, District.KH, District.ME, District.MB, District.NB, District.PY, District.PZ, District.PB, District.RA),
    US(District.DC, District.CV, District.LT, District.LN, District.MO, District.TP, District.UL),
    VY(District.HB, District.JI, District.PE, District.TR, District.ZR),
    ZL(District.KM, District.UH, District.VS, District.ZL),
    CZ(District.values());

    private District[] districts;

    Region(final District... districts) {
        this.districts = districts;
    }

    public District[] getDistricts() {
        return districts;
    }

}
