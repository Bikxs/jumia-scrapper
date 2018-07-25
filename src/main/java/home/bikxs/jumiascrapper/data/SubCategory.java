package home.bikxs.jumiascrapper.data;

public class SubCategory {
    private String name;
    private String href;

    public SubCategory(String name, String href) {
        this.name = name;
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    @Override
    public String toString() {
        return name + " " + href;
    }
}
