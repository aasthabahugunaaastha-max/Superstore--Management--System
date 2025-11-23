import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class EOQTest {
    @Test
    public void testEOQ() {
        Item item = new Item("Laptop", "E001", 100, 2000, 50);
        double expectedEOQ = Math.sqrt((2 * 100 * 2000) / 50);
        assertEquals(expectedEOQ, item.calculateEOQ(), 0.001);
    }
}
