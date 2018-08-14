package fogbow.billing.datastore;

import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuthDataStoreTest {
	
	private static String databaseFile = "authDataStoreTest.sqlite3";
    private static String databaseURL = "jdbc:sqlite:" + databaseFile;

    private static Properties properties;

    private AuthDataStore authDataStore;
    
    private static String user1 = "fake-id-1";
    private static String user2 = "fake-id-2";
    private static String user3 = "fake-id-3";
    
    private static final int AUTHORIZED_USER = 1;
    private static final int UNAUTHORIZED_USER = 0;
    private static final int UNEXPECTED_AUTHORIZATION = -1;
    
    @Before
    public void setUp() {
    	
    	properties = new Properties();
    	properties.setProperty(AuthDataStore.DATABASE_URL_PROP, databaseURL);
    	
    	authDataStore = new AuthDataStore(properties);
    }
    
    @After
    public void tearDown() {
        //new File(databaseFile).delete();
    }
    
    // test case: Adding new user in authDataStore. The addition must be successfully 
    // and the user is not authorized 
    @Test
    public void testAddUser() {
    	
    	// exercise
    	boolean result = authDataStore.addUser(user1);
    	int authorization = authDataStore.getUserAuthorization(user1, "compute", "create");
    	
    	// verify
    	Assert.assertTrue(result);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization);
    }
    
    
    // test case: Adding new user in authDataStore. The addition must be successfully 
    // and the user is not authorized. After try to add the same user, the store must return false
    @Test
    public void testAddSameUser() {
    	
    	// exercise
    	boolean result = authDataStore.addUser(user1);
    	int authorization = authDataStore.getUserAuthorization(user1, "compute", "create");
    	boolean resultSameUser = authDataStore.addUser(user1);
    	
    	// verify
    	Assert.assertTrue(result);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization);
    	Assert.assertFalse(resultSameUser);
    }
    
    // test case: Adding multiple users in authDataStore and checking if all are unauthorized.
    @Test
    public void testAddMultipleUsers() {
    	
    	// exercise
    	boolean result1 = authDataStore.addUser(user1);
    	boolean result2 = authDataStore.addUser(user2);
    	boolean result3 = authDataStore.addUser(user3);
    	int authorization1 = authDataStore.getUserAuthorization(user1, "compute", "create");
    	int authorization2 = authDataStore.getUserAuthorization(user2, "compute", "create");
    	int authorization3 = authDataStore.getUserAuthorization(user2, "compute", "create");
    	
    	// verify
    	Assert.assertTrue(result1);
    	Assert.assertTrue(result2);
    	Assert.assertTrue(result3);
    	
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization1);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization2);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization3);
    }
    
    // test case: Adding multiple users in authDataStore, authorizing one of them and checking the authorization
    @Test
    public void testAuthorizeUser() {
    	
    	// exercise
    	boolean result1 = authDataStore.addUser(user1);
    	boolean result2 = authDataStore.addUser(user2);
    	boolean result3 = authDataStore.addUser(user3);
    	boolean resultAuthorization = authDataStore.authorizeUser(user1);
    	
    	int authorization1 = authDataStore.getUserAuthorization(user1, "compute", "create");
    	int authorization2 = authDataStore.getUserAuthorization(user2, "compute", "create");
    	int authorization3 = authDataStore.getUserAuthorization(user2, "compute", "create");
    	
    	// verify
    	Assert.assertTrue(result1);
    	Assert.assertTrue(result2);
    	Assert.assertTrue(result3);
    	Assert.assertTrue(resultAuthorization);
    	
    	Assert.assertEquals(AUTHORIZED_USER, authorization1);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization2);
    	Assert.assertEquals(UNAUTHORIZED_USER, authorization3);
    }
    
    // test case: Adding a new in authDataStore, authorizing it and unauthorizing after that.
    @Test
    public void testUnauthorizeUser() {
    	
    	// exercise
    	boolean addResult = authDataStore.addUser(user1);
    	boolean authorizationResult = authDataStore.authorizeUser(user1);
    	int authorization = authDataStore.getUserAuthorization(user1, "compute", "create");
    	
    	// verify
    	Assert.assertTrue(addResult);
    	Assert.assertTrue(authorizationResult);
    	Assert.assertEquals(AUTHORIZED_USER, authorization);
    	
    	// exercise
    	boolean unauthorizationResult = authDataStore.unauthorizeUser(user1);
    	int newAuthorization = authDataStore.getUserAuthorization(user1, "compute", "create");
    	
    	// verify
    	Assert.assertTrue(unauthorizationResult);
    	Assert.assertEquals(UNAUTHORIZED_USER, newAuthorization);
    }
    
    // test case: Getting an authorization of inexistent user.
    @Test
    public void testGetAuthorizationNotFoundUser() {
    	
    	// exercise
    	int authorization = authDataStore.getUserAuthorization("fake-id-4", "compute", "create");
    	
    	// verify
    	Assert.assertEquals(UNEXPECTED_AUTHORIZATION, authorization);
    }
    
    // test case: Authorizing an inexistent user.
    @Test
    public void testAuthorizeNotFoundUser() {
    	
    	// exercise
    	boolean authorizationResult = authDataStore.authorizeUser("fake-id-4");
    	
    	// verify
    	Assert.assertTrue(authorizationResult);
    }
    
    // test case: Unauthorizing an inexistent user.
    @Test
    public void testUnauthorizeNotFoundUser() {
    	
    	// exercise
    	boolean authorizationResult = authDataStore.unauthorizeUser("fake-id-4");
    	
    	// verify
    	Assert.assertTrue(authorizationResult);
    }

}
