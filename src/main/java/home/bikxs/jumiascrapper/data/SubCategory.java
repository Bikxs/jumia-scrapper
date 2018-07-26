package home.bikxs.jumiascrapper.data;

public class SubCategory {
    private String category;
    private String name;
    private String href;

    public SubCategory(String category,String name, String href) {
        this.category =category;
        this.name = name;
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public String getHref() {
        return href;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name + " " + href;
    }
}
