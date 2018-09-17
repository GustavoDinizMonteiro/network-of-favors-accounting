package fogbow.billing.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fogbow.billing.datastore.AuthDataStore;

public class AuthorizationServiceTest {
	
	private AuthDataStore authDataStore;
	private AuthorizationDirectoryServiceImpl authService;

	private final static String TOKEN_PROVIDER = "fake-token-provider";
    private final static String USER_1 = "fake-id-1";
    
    private final static String COMPUTE_RESOURCE = "compute";
    private final static String CREATE_OPERATION = "create";
    
    @Before
    public void setUp() {
    	
    	authService = Mockito.spy(AuthorizationDirectoryServiceImpl.getInstance()); 	
    	authDataStore = Mockito.mock(AuthDataStore.class);
    	
        Mockito.when(authService.getAuthDataStore()).thenReturn(authDataStore);
    	
    }
    
    // test case: When the dataStore returns successfully addition of a new user, the service 
    // must return the same
    @Test
    public void testAddUserSucessfully() {
    	
    	// set up
    	Mockito.when(authDataStore.addUser(Mockito.any(String.class))).thenReturn(true);
    	
    	// exercise
    	boolean result = authService.addUser(USER_1);
    	
    	// verify
    	Assert.assertTrue(result);
    }
    
    // test case: When the dataStore returns failed addition of a new user, the service 
    // must return the same
    @Test
    public void testAddUserFailed() {
    	
    	// set up
    	Mockito.when(authDataStore.addUser(Mockito.any(String.class))).thenReturn(false);
    	
    	// exercise
    	boolean result = authService.addUser(USER_1);
    	
    	// verify
    	Assert.assertFalse(result);
    }
    
    
    // test case: When the dataStore returns 0, the user is not authorized
    @Test
    public void testGetUserAuthorizationFailed() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(0);
    	
    	// exercise
    	boolean result = authService.getUserAuthorization(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertFalse(result);
    }
    
    // test case: When the dataStore returns -1, the user doesn't exists.
    @Test
    public void testGetUserAuthorizationFailed2() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(-1);
    	
    	// exercise
    	boolean result = authService.getUserAuthorization(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertFalse(result);
    }
	
    // test case: When the dataStore returns 1, the user is authorized
    @Test
    public void testGetUserAuthorizationSuccessuly() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(1);
    	
    	// exercise
    	boolean result = authService.getUserAuthorization(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertTrue(result);
    }
    
    // test case: When the user is authorized, it can be unauthorized.
    @Test
    public void testGetUserUnauthorizationSuccessfully() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(1);
    	Mockito.when(authDataStore.unauthorizeUser(USER_1)).thenReturn(true);
    	
    	// exercise
    	boolean result = authService.unauthorizeUser(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertTrue(result);
    }
    
    // test case: When the user is already unauthorized, it can't be unauthorized.
    @Test
    public void testGetUserUnauthorizationFailed() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(0);
    	Mockito.when(authDataStore.unauthorizeUser(USER_1)).thenReturn(true);
    	
    	// exercise
    	boolean result = authService.unauthorizeUser(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertFalse(result);
    }
    
    // test case: When the user is unauthorized, it can be authorized.
    @Test
    public void testGetUserAuthorizationSuccessfully() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(0);
    	Mockito.when(authDataStore.authorizeUser(USER_1)).thenReturn(true);
    	
    	// exercise
    	boolean result = authService.authorizeUser(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertTrue(result);
    }
    
    // test case: When the user is already authorized, it can't be authorized.
    @Test
    public void testGetAuthorizationFailed() {
    	
    	// set up
    	Mockito.when(authDataStore.getUserAuthorization(Mockito.any(String.class), Mockito.any(String.class),Mockito.any(String.class))).thenReturn(1);
    	Mockito.when(authDataStore.authorizeUser(USER_1)).thenReturn(true);
    	
    	// exercise
    	boolean result = authService.authorizeUser(TOKEN_PROVIDER, USER_1, COMPUTE_RESOURCE, CREATE_OPERATION);
    	
    	// verify
    	Assert.assertFalse(result);
    }
	

}
