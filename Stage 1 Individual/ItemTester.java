import java.util.ArrayList;
import java.util.List;

/** Independent driver: failures throw AssertionError, even without java -ea. */
public class ItemTester {
    private static int passed;

    private static void check(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError("FAIL: " + label);
        }
        passed++;
        System.out.printf("PASS %02d: %s%n", passed, label);
    }

    private static void rejects(String label, Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            check(label, true);
            return;
        }
        throw new AssertionError("FAIL: " + label + " (no exception)");
    }

    private static void testWishlist(Product p1, Product p2) {
        System.out.println("\nWishlistItem unit tests");
        WishlistItem item = new WishlistItem(p1, 5);
        check("constructor keeps the exact Product reference", item.getProduct() == p1);
        check("constructor stores quantity 5", item.getQuantity() == 5);
        item.setQuantity(7);
        check("setting 7 replaces 5, rather than producing 12", item.getQuantity() == 7);
        item.setQuantity(1);
        check("quantity can decrease to the minimum valid value 1", item.getQuantity() == 1);
        item.setQuantity(Integer.MAX_VALUE);
        check("maximum positive int is accepted", item.getQuantity() == Integer.MAX_VALUE);
        item.setQuantity(5);
        rejects("null product rejected", () -> new WishlistItem(null, 5));
        for (int invalid : new int[] {0, -1, Integer.MIN_VALUE}) {
            rejects("constructor rejects quantity " + invalid, () -> new WishlistItem(p1, invalid));
            rejects("setter rejects quantity " + invalid, () -> item.setQuantity(invalid));
            check("failed update leaves quantity 5 and product unchanged",
                    item.getQuantity() == 5 && item.getProduct() == p1);
        }
        WishlistItem otherClient = new WishlistItem(p1, 7);
        WishlistItem otherProduct = new WishlistItem(p2, 6);
        item.setQuantity(9);
        check("entries sharing a product keep independent quantities", otherClient.getQuantity() == 7);
        check("different-product entry remains unchanged",
                otherProduct.getProduct() == p2 && otherProduct.getQuantity() == 6);
        check("wishlist changes do not alter stock or price",
                p1.getAmountInStock() == 10 && p1.getSalePrice() == 1.0);
    }

    // These lists stand in for client-owned lists; they are not the group Client class.
    private static void testClientLists() {
        System.out.println("\nClient-owned list simulation using the assignment quantities");
        Product[] products = new Product[5];
        for (int i = 0; i < products.length; i++) {
            products[i] = new Product("P" + (i + 1), "Product " + (i + 1), (i + 1) * 10, i + 1);
        }
        List<WishlistItem> c1 = new ArrayList<>();
        List<WishlistItem> c2 = new ArrayList<>();
        List<WishlistItem> c3 = new ArrayList<>();
        for (int i : new int[] {0, 2, 4}) { c1.add(new WishlistItem(products[i], 5)); }
        for (int i : new int[] {0, 1, 3}) { c2.add(new WishlistItem(products[i], 7)); }
        for (int i : new int[] {0, 1, 4}) { c3.add(new WishlistItem(products[i], 6)); }
        check("C1 has P1, P3, P5 with quantity 5", describe(c1).equals("P1=5 P3=5 P5=5"));
        check("C2 initially has P1, P2, P4 with quantity 7", describe(c2).equals("P1=7 P2=7 P4=7"));
        check("C3 has P1, P2, P5 with quantity 6", describe(c3).equals("P1=6 P2=6 P5=6"));
        System.out.println("C1: " + describe(c1));
        System.out.println("C2 before: " + describe(c2));
        System.out.println("C3 before: " + describe(c3));
        c2.add(new WishlistItem(products[2], 7));
        c2.add(new WishlistItem(products[4], 7));
        check("C2 adds P3 and P5 while retaining prior entries",
                describe(c2).equals("P1=7 P2=7 P4=7 P3=7 P5=7"));
        check("C3 is unchanged after additions to C2", describe(c3).equals("P1=6 P2=6 P5=6"));
        check("C1 is unchanged after additions to C2", describe(c1).equals("P1=5 P3=5 P5=5"));
        System.out.println("C2 after: " + describe(c2));
        System.out.println("C3 after: " + describe(c3));
        c2.get(0).setQuantity(3);
        check("replacing C2's P1 quantity keeps five entries and sets quantity 3",
                c2.size() == 5 && c2.get(0).getQuantity() == 3);
        check("replacing C2's P1 quantity does not affect C1 or C3",
                c1.get(0).getQuantity() == 5 && c3.get(0).getQuantity() == 6);
        for (int i = 0; i < products.length; i++) {
            check(products[i].getId() + " stock and price unchanged",
                    products[i].getAmountInStock() == (i + 1) * 10 && products[i].getSalePrice() == i + 1);
        }
    }

    private static String describe(List<WishlistItem> items) {
        StringBuilder result = new StringBuilder();
        for (WishlistItem item : items) {
            if (result.length() > 0) { result.append(' '); }
            result.append(item.getProduct().getId()).append('=').append(item.getQuantity());
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println("CSCI 430 Group 8 - Minh Quan Tran - Individual tests");
        Product p1 = new Product("P1", "Product 1", 10, 1.0);
        Product p2 = new Product("P2", "Product 2", 20, 2.0);
        testWishlist(p1, p2);
        testClientLists();
        System.out.printf("%nRESULT: %d passed, 0 failed%n", passed);
    }
}
