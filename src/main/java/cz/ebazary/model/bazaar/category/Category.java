package cz.ebazary.model.bazaar.category;

public enum Category {

    cars("Automobily"),
    motorcycles("Motocykly"),
    machines("Stroje, nástroje"),
    children("Děti"),
    home("Dům, byt zahrada"),
    animals("Zvířata"),
    sport("Sport, hobby"),
    culture("Kultura"),
    books("Knihy"),
    fashion("Móda"),
    computers("Počítače"),
    mobile_devices("Mobilní zařízení"),
    appliances("Elektro"),
    audio_video("Audio, video"),
    photo("Foto");

    private final String name;

    Category(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
