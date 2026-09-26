/**
 * This class use as test support for WishlistItem
 * This is not the real class for Product in the group project
 */
public class Product {
    private final String id;
    private final String name;
    private final int amountInStock;
    private final double salePrice;

    public Product(String id, String name, int amountInStock, double salePrice) {
        this.id = id;
        this.name = name;
        this.amountInStock = amountInStock;
        this.salePrice = salePrice;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAmountInStock() { return amountInStock; }
    public double getSalePrice() { return salePrice; }
}
