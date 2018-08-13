package fogbow.billing.services;

import org.junit.Before;
import org.mockito.Mockito;

import fogbow.billing.datastore.AuthDataStore;

public class AuthorizationServiceTest {
	
	private AuthDataStore authDataStore;
	private AuthorizationDirectoryServiceImpl authService;
    
    private static String user1 = "fake-id-1";
    private static String user2 = "fake-id-2";
    private static String user3 = "fake-id-3";
    
    @Before
    public void setUp() {
    	
    	authService = Mockito.spy(AuthorizationDirectoryServiceImpl.getInstance()); 	
    	authDataStore = Mockito.mock(AuthDataStore.class);
    	
    	Mockito.when(authService.getAuthDataStore()).thenReturn(authDataStore);
        Mockito.when(authService.getAuthDataStore()).thenReturn(authDataStore);
    	
    }
	
	

}
