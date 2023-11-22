import com.chupaniko.dataworker.EntityType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EntityTypeTest {
    @Test
    public void entityTypeStringShouldBeEqualToKey() {
        Assertions.assertEquals(EntityType.DEVICE.toString(), "DEVICE");
    }
}
