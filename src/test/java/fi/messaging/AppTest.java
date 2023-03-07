package fi.messaging;

import static org.junit.Assert.assertTrue;

import org.junit.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
	@BeforeClass
	public static void setUp()
	{
		System.out.println("test begins ...");
	}
	
	@AfterClass
	public static void finished()
	{
		System.out.println("Tests finished!");
	}
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
}
