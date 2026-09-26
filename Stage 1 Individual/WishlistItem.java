/** One product and the quantity desired by a client. */
public class WishlistItem {
    private final Product product;
    private int quantity;

    /**
     * Creates an entry without reserving or changing product stock.
     * @throws IllegalArgumentException if product is null or quantity is not positive
     */
    public WishlistItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.product = product;
        this.quantity = quantity;
    }

    /** Replaces the desired quantity; it does not add to the previous value. */
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }
}
