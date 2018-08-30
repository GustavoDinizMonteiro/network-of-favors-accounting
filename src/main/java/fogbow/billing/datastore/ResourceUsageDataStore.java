package fogbow.billing.datastore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.fogbowcloud.ras.core.models.ResourceType;

import fogbow.billing.datastore.commands.SQLCommands;
import fogbow.billing.datastore.commands.TimestampTableAttributes;
import fogbow.billing.model.OrderRecord;

public class ResourceUsageDataStore extends DataStore {
	
	//TODO Remove it and use properties
	public static final String USAGE_DATABASE_URL = "jdbc:sqlite:/local/mafra/fogbow/usage_database.sqlite3";
	
	//TODO Remove it and use properties
	public static final String RAS_DATABASE_URL = "jdbc:sqlite:/local/mafra/orderStorageTest.sqlite3";
	
	public static final String DATABASE_URL_PROP = "usage_database_url";
	
	
	public static final String RAS_URL_PROP = "ras_database_url";
	
	private String ras_database_url;
	

	public ResourceUsageDataStore() throws SQLException {
		super(USAGE_DATABASE_URL);

		Statement statement = null;
		Connection connection = null;
		try {
			
			connection = getConnection();
			statement = connection.createStatement();
			statement.execute(SQLCommands.CREATE_TABLE_SQL);
			
		} catch (SQLException e) {
            throw new SQLException(e);
			
		} finally {
			close(statement, connection);
		}
	}
	
	
	public ResourceUsageDataStore(Properties properties) throws SQLException {
		super(properties.getProperty(DATABASE_URL_PROP));
		
		this.ras_database_url = properties.getProperty(RAS_URL_PROP);
		
		Statement statement = null;
		Connection connection = null;
		try {

			connection = getConnection();
			statement = connection.createStatement();
			statement.execute(SQLCommands.CREATE_TABLE_SQL);
			
		} catch (SQLException e) {
            throw new SQLException(e);
			
		} finally {
			close(statement, connection);
		}
	
	}
	
	
	protected void preProcessRASDatabase() throws SQLException {
		PreparedStatement selectMemberStatement = null;

        Connection connection = null;
        
        List<OrderRecord> listOfRecords = new ArrayList<>();
        
        OrderRecord record;

        try {
            connection = getConnection(getRASDatabaseURL());
            connection.setAutoCommit(false);

            selectMemberStatement = connection
                    .prepareStatement(SQLCommands.SELECT_DURATION_SQL);
            
            ResultSet rs = selectMemberStatement.executeQuery();
            
            while (rs.next()) {
                String orderId = rs.getString(TimestampTableAttributes.ORDER_ID);
                String resourceType = rs.getString(TimestampTableAttributes.RESOURCE_TYPE);
                String usage = rs.getString(TimestampTableAttributes.USAGE);
                String userId = rs.getString(TimestampTableAttributes.FEDERATION_USER_ID);
                String userName = rs.getString(TimestampTableAttributes.FEDERATION_USER_NAME);
                String requestingMember = rs.getString(TimestampTableAttributes.REQUESTING_MEMBER);
                String providingMember = rs.getString(TimestampTableAttributes.PROVIDING_MEMBER);
                Timestamp startTime = rs.getTimestamp(TimestampTableAttributes.START_TIME);
                int duration = rs.getInt(TimestampTableAttributes.DURATION);
                
                record = new OrderRecord(orderId, resourceType, usage, userId, userName, requestingMember, providingMember, startTime, duration);
                listOfRecords.add(record);                
            }
            connection.commit();

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("Couldn't rollback transaction.");
            }

        } finally {
            close(selectMemberStatement, connection);
        }
        
        for (OrderRecord orderRecord: listOfRecords) {
        	insertOrderRecord(orderRecord);
        }
	}
	
	protected void insertOrderRecord(OrderRecord orderRecord) throws SQLException{
		
		Connection connection = null;
        PreparedStatement orderStatement = null;

        try {
        	
            connection = getConnection();
            connection.setAutoCommit(false);

            orderStatement = connection.prepareStatement(SQLCommands.INSERT_TIMESTAMP_SQL);

            orderStatement.setString(1, orderRecord.getOrderId());
            orderStatement.setString(2, orderRecord.getResourceType());
            orderStatement.setString(3, orderRecord.getUsage());
            orderStatement.setString(4, orderRecord.getUserId());
            orderStatement.setString(5, orderRecord.getUserName());
            orderStatement.setString(6, orderRecord.getRequestingMember());
            orderStatement.setString(7, orderRecord.getProvidingMember());
            orderStatement.setTimestamp(8, orderRecord.getStart_time());
            orderStatement.setInt(9, orderRecord.getDuration());

            orderStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                throw e1;
            }

            throw e;
        } finally {
            close(orderStatement, connection);
        }
				
	}
	
	public List<OrderRecord> getOrdersByUserId(String userId){
		PreparedStatement selectMemberStatement = null;

        Connection connection = null;
        
        OrderRecord entry;
        List<OrderRecord> allOrders = new ArrayList<>();
        
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            selectMemberStatement = connection
                    .prepareStatement(SQLCommands.SELECT_ORDERS_BY_USER_SQL);

            selectMemberStatement.setString(1, userId);

            ResultSet rs = selectMemberStatement.executeQuery();
            while (rs.next()) {
            	String orderId = rs.getString(TimestampTableAttributes.ORDER_ID);
                String resourceType = rs.getString(TimestampTableAttributes.RESOURCE_TYPE);
                String usage = rs.getString(TimestampTableAttributes.USAGE);
                String fedUserId = rs.getString(TimestampTableAttributes.FEDERATION_USER_ID);
                String userName = rs.getString(TimestampTableAttributes.FEDERATION_USER_NAME);
                String requestingMember = rs.getString(TimestampTableAttributes.REQUESTING_MEMBER);
                String providingMember = rs.getString(TimestampTableAttributes.PROVIDING_MEMBER);
                Timestamp startTime = rs.getTimestamp(TimestampTableAttributes.START_TIME);
                int duration = rs.getInt(TimestampTableAttributes.DURATION);
                
                entry = new OrderRecord(orderId, resourceType, usage, fedUserId, userName, requestingMember, providingMember, startTime, duration);
                allOrders.add(entry);
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("Couldn't rollback transaction.");
            }

        } finally {
            close(selectMemberStatement, connection);
        }

        return allOrders;
	}
	
	public List<OrderRecord> getOrdersByUserIdAndResourceType(String userId, ResourceType resourceType){
		PreparedStatement selectMemberStatement = null;

        Connection connection = null;
        
        OrderRecord entry;
        List<OrderRecord> allOrders = new ArrayList<>();
        
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            selectMemberStatement = connection
                    .prepareStatement(SQLCommands.SELECT_ORDERS_BY_USER_AND_RESOURCE_TYPE_SQL);

            selectMemberStatement.setString(1, userId);
            selectMemberStatement.setString(2, String.valueOf(resourceType));

            ResultSet rs = selectMemberStatement.executeQuery();
            while (rs.next()) {
            	String orderId = rs.getString(TimestampTableAttributes.ORDER_ID);
                String resource = rs.getString(TimestampTableAttributes.RESOURCE_TYPE);
                String usage = rs.getString(TimestampTableAttributes.USAGE);
                String fedUserId = rs.getString(TimestampTableAttributes.FEDERATION_USER_ID);
                String userName = rs.getString(TimestampTableAttributes.FEDERATION_USER_NAME);
                String requestingMember = rs.getString(TimestampTableAttributes.REQUESTING_MEMBER);
                String providingMember = rs.getString(TimestampTableAttributes.PROVIDING_MEMBER);
                Timestamp startTime = rs.getTimestamp(TimestampTableAttributes.START_TIME);
                int duration = rs.getInt(TimestampTableAttributes.DURATION);
                
                entry = new OrderRecord(orderId, resource, usage, fedUserId, userName, requestingMember, providingMember, startTime, duration);
                allOrders.add(entry);
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                System.out.println("Couldn't rollback transaction.");
            }

        } finally {
            close(selectMemberStatement, connection);
        }

        return allOrders;
	}
	
	public String getRASDatabaseURL() {
		//return RAS_DATABASE_URL;
		return this.ras_database_url;
	}
	
}
