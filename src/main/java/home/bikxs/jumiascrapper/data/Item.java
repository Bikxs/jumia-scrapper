package home.bikxs.jumiascrapper.data;

public class Item {
    private SubCategory subCategory;
    private String description;
    private String href;
    private Double price;
    private Double discount;

    public Item(SubCategory subCategory, String description, String href, Double price, Double discount) {
        this.subCategory = subCategory;
        this.description = description;
        this.href = href;
        this.price = price;
        this.discount = discount;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public String getDescription() {
        return description;
    }

    public String getHref() {
        return href;
    }

    public Double getPrice() {
        return price;
    }

    public Double getDiscount() {
        return discount;
    }

    @Override
    public String toString() {
        return description + " " + price;
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        sb.append(subCategory.getName().replace(";", "")).append(";");
        sb.append(description.replace(";", "")).append(";");
        sb.append(price).append(";");
        sb.append(discount);
        return sb.toString();
    }
}
