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

import org.fogbowcloud.ras.core.datastore.commands.TimestampSQLCommands;

import fogbow.billing.datastore.commands.SQLCommands;
import fogbow.billing.datastore.commands.TimestampTableAttributes;
import fogbow.billing.model.TimestampTableEntry;

public class ResourceUsageDataStore extends DataStore {
	
	//TODO Remove it and use properties
	public static final String USAGE_DATABASE_URL = "jdbc:sqlite:/local/mafra/usage_database.sqlite3";
	
	public static final String RAS_DATABASE_URL = "jdbc:sqlite:/local/mafra/orderStorageTest.sqlite3";
	
	public static final String DATABASE_URL_PROP = "usage_database_url";
	

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
		
		preProcessRASDatabase();
	}
	
	public ResourceUsageDataStore(Properties properties) throws SQLException {
		super(properties.getProperty(DATABASE_URL_PROP));

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
	
	
	private void preProcessRASDatabase() throws SQLException {
		PreparedStatement selectMemberStatement = null;

        Connection connection = null;
        
        List<TimestampTableEntry> listOfEntries = new ArrayList<>();
        
        TimestampTableEntry entry;

        try {
            connection = getConnection(RAS_DATABASE_URL);
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
                
                entry = new TimestampTableEntry(orderId, resourceType, usage, userId, userName, requestingMember, providingMember, startTime, duration);
                listOfEntries.add(entry);                
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
        
        for (TimestampTableEntry timestampEntry: listOfEntries) {
        	insertTimestampleTableEntry(timestampEntry);
        }
	}
	
	private void insertTimestampleTableEntry(TimestampTableEntry entry) throws SQLException{
		
		Connection connection = null;
        PreparedStatement orderStatement = null;

        try {
        	
            connection = getConnection();
            connection.setAutoCommit(false);

            orderStatement = connection.prepareStatement(SQLCommands.INSERT_TIMESTAMP_SQL);

            orderStatement.setString(1, entry.getOrderId());
            orderStatement.setString(2, entry.getResourceType());
            orderStatement.setString(3, entry.getUsage());
            orderStatement.setString(4, entry.getUserId());
            orderStatement.setString(5, entry.getUserName());
            orderStatement.setString(6, entry.getRequestingMember());
            orderStatement.setString(7, entry.getProvidingMember());
            orderStatement.setTimestamp(8, entry.getStart_time());
            orderStatement.setInt(9, entry.getDuration());

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
}
