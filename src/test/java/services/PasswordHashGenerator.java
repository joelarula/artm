package services;

import junit.framework.TestCase;

import org.eclipse.jetty.util.security.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordHashGenerator extends TestCase{
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordHashGenerator.class);
	
	public void testGenerate(){
		Password.main(new String[]{"test","xxx"});		
	}

}
