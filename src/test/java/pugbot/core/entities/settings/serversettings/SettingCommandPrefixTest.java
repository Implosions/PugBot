package pugbot.core.entities.settings.serversettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pugbot.core.exceptions.InvalidUseException;

public class SettingCommandPrefixTest {
	
	private SettingCommandPrefix setting;
	
	@BeforeEach
	public void setup() {
		setting = new SettingCommandPrefix(0, new String());
	}
	
	@Test
	public void setThrowsInvalidUseExceptionIfArgsLengthIsGreaterThanOne() {
		String[] args = "test input".split(" ");
		
		assertThrows(InvalidUseException.class, () -> setting.set(args));
	}
	
	@Test
	public void setThrowsInvalidUseExceptionIfNewPrefixLengthIsGreaterThanThree() {
		String prefix = "!!!!";
		String[] args = new String[] { prefix };
		
		assertThrows(InvalidUseException.class, () -> setting.set(args));
	}
	
	@Test
	public void setSetsTheValueIfAValidInputIsGiven() {
		String prefix = "@";
		String[] args = new String[] { prefix };
		
		setting.set(args);
		
		assertEquals(prefix, setting.getValue());
	}
}
