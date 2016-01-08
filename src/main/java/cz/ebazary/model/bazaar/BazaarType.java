package cz.ebazary.model.bazaar;

public enum BazaarType {

    sbazar("sbazar.cz"),
    bazos("bazos.cz"),
    bazar("bazar.cz");

    private String name;

    BazaarType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
