package fogbow.billing.datastore.commands;

public class SQLCommands extends TimestampTableAttributes{
	
	public static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS " + TIMESTAMP_TABLE_NAME
            + "(" + ORDER_ID + " VARCHAR(255), " 
            + RESOURCE_TYPE + " VARCHAR(255), "
            + SPEC + " VARCHAR(255), "
    		+ FEDERATION_USER_ID + " VARCHAR(255), " 
            + FEDERATION_USER_NAME + " VARCHAR(255), " 
    		+ REQUESTING_MEMBER + " VARCHAR(255), "
            + PROVIDING_MEMBER + " VARCHAR(255), " 
            + START_TIME + " TIMESTAMP, " 
    		+ DURATION + " INTEGER, PRIMARY KEY (" + ORDER_ID + "))";
	
	public static final String INSERT_TIMESTAMP_SQL = "INSERT INTO " + TIMESTAMP_TABLE_NAME
            + " (" + ORDER_ID + "," + RESOURCE_TYPE + "," + SPEC + ","
    		+ FEDERATION_USER_ID + "," + FEDERATION_USER_NAME + ","
            + REQUESTING_MEMBER + "," + PROVIDING_MEMBER + "," + START_TIME + "," + DURATION + ")"
            + " VALUES (?,?,?,?,?,?,?,?,?)";
	
	public static final String SELECT_ORDERS_BY_USER_SQL = "SELECT * FROM " + TIMESTAMP_TABLE_NAME + 			
             " WHERE " + FEDERATION_USER_ID + "=?";
	
	public static final String SELECT_ORDERS_SQL = "SELECT * FROM " + TIMESTAMP_TABLE_NAME + 			
            " WHERE " + FEDERATION_USER_ID + "=? AND "
            + REQUESTING_MEMBER + "=? AND "
            + PROVIDING_MEMBER + "=? AND "
            + RESOURCE_TYPE + "=?";
	
	public static final String SELECT_ORDERS_BY_USER_AND_RESOURCE_TYPE_SQL = "SELECT * FROM " + TIMESTAMP_TABLE_NAME + 			
            " WHERE " + FEDERATION_USER_ID + "=? AND " + RESOURCE_TYPE + "=?";
	
	public static final String SELECT_DURATION_SQL = "SELECT t1.order_id AS order_id, t1.resource_type AS resource_type, t1.usage AS usage, t1.fed_user_id AS fed_user_id, t1.fed_user_name AS fed_user_name, t1.requesting_member AS requesting_member, t1.providing_member AS providing_member, t1.timestamp AS start_time, t2.timestamp - t1.timestamp AS duration FROM (SELECT * FROM timestamp t1 WHERE t1.order_state = 'FULFILLED') t1 LEFT JOIN (SELECT * FROM timestamp t2 WHERE t2.order_state = 'CLOSED') t2 ON t1.order_id = t2.order_id;";

}
