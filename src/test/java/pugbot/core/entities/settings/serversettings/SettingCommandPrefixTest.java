package pugbot.core.entities.settings.serversettings;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import pugbot.core.exceptions.InvalidUseException;

public class SettingCommandPrefixTest {
	
	private SettingCommandPrefix setting;
	
	@Before
	public void setup() {
		setting = new SettingCommandPrefix(0, new String());
	}
	
	@Test(expected = InvalidUseException.class)
	public void setThrowsInvalidUseExceptionIfArgsLengthIsGreaterThanOne() {
		String[] args = "test input".split(" ");
		
		setting.set(args);
	}
	
	@Test(expected = InvalidUseException.class)
	public void setThrowsInvalidUseExceptionIfNewPrefixLengthIsGreaterThanThree() {
		String prefix = "!!!!";
		String[] args = new String[] { prefix };
		
		setting.set(args);
	}
	
	@Test
	public void setSetsTheValueIfAValidInputIsGiven() {
		String prefix = "@";
		String[] args = new String[] { prefix };
		
		setting.set(args);
		
		assertEquals(prefix, setting.getValue());
	}
}
