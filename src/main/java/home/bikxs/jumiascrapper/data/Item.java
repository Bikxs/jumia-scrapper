package home.bikxs.jumiascrapper.data;

import home.bikxs.jumiascrapper.AnsiColors;

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
        if (discount == null) return 0.0;
        return Math.abs(discount);
    }

    @Override
    public String toString() {
        return AnsiColors.cyan(description) + " " + AnsiColors.purple("" + price) + " " + AnsiColors.blue(href);
    }

    public String toCSVString() {
        StringBuilder sb = new StringBuilder();
        sb.append(qoute(subCategory.getCategory().replace(";", ""))).append(",");
        sb.append(qoute(subCategory.getName().replace(";", ""))).append(",");
        sb.append(qoute(description.replace(";", ""))).append(",");
        sb.append(price).append(",");
        sb.append(getDiscount()).append(",");
        sb.append(href);
        return sb.toString();
    }

    private String qoute(String input) {
        return '"' + input.replace(",", "").replace('"', '\'') + '"';
    }

    public boolean isTreasure() {
        if ((this.getDiscount() >= 99.0)) {
            if (getDescription().length() > 50) {
                String name = getDescription().substring(0, 50);
                switch (name) {
                    case "Generic Hands-free Wireless Bluetooth Earphone Hea":
                    case "Fashion Antique Silver Charm Bracelet & Bangle Wit":
                    case "Fashion Home Toepost Flip Flops Women Flowers Sand":
                    case "Fashion Women Flowers Sandal Home Toepost Flip Flo":
                    case "Generic Bluetooth Min Speaker BM3D Subwoofer Hand-":
                    case "Fashion BAMOER 5 Style Silver Color LOVE Snake Cha":
                    case "Generic Mifa F1 Outdoor Portable Bluetooth Speaker":
                    case "Generic Bluetooth 4.0 Speaker With LED Light For S":
                    case "Generic Bluetooth Speaker LED Portable Wireless Sp":
                    case "Generic Mifa Wireless Bluetooth Speaker Waterproof":
                    case "Fashion Luxury Chain Link Bracelet For Women Ladie":
                    case "Generic Wireless Bluetooth Speaker Waterproof Port":
                    case "Generic Element T2 Bluetooth 4.2 Outdoor Water Res":

                        return false;
                }
            }

            if (!(getDescription().contains("Purse")))
                if (!(this.getDescription().contains("Nubia M2 LITE 3GB RAM 64GB")))
                    if (!(this.getDescription().contains("Superman Cap Adjustable Baseball")))
                        return true;
        }
        return false;
    }

    public boolean isBargain() {
        if ((this.getDiscount() >= 70.0 && this.getPrice() >= 950.0)) {
            switch (subCategory.getCategory()) {
                case "Audio":
                case "Automotive":
                case "Cameras":
                case "Computing":
                case "Desktops":
                case "Fitness & Accessories":
                case "Gaming":
                case "Home Theaters & Speakers":
                case "Laptops":
                case "Large Appliances":
                case "Mobile Phones":
                case "Peripherals & Accessories":
                case "Printers":
                case "Printers & Scanners":
                case "Storage":
                case "Tablets":
                case "Televisions":
                case "Video & Audio":
                case "Watches":
                    return true;
            }
        }
        return false;
    }
}
