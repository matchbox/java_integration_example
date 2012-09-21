package net.matchbox;
import java.io.IOException;
import java.util.Date;
import net.matchbox.api.v1.Connection;
import org.json.JSONObject;

public class SyncProcess {	
	public static void main(String[] args) throws InvalidFileFormatException, IOException {
		SyncProcess process = new SyncProcess();
		process.sync();
    }
	public SyncProcess() throws InvalidFileFormatException, IOException{
	}
	public void sync() throws IOException{
		Connection c = new Connection("joe_user@school.edu", "mypass");
		try{
			for(JSONObject o : c.getApplications()){
				saveAssignmentJsonObject(o);
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	private void saveAssignmentJsonObject(JSONObject o){
		// try{
		// 	String query = "UPSERT INTO MY_TABLE (FIELDS) VALUES(VALUE)";
		// 	PreparedStatement ps = connection.prepareStatement(query);
		// 	ps.execute();
		// 	int updatedCount = ps.getUpdateCount();
		// 	if(updatedCount != 1){
		// 		System.out.println("ERROR: in executing the SQL statement there were " + updatedCount + " records updated!");
		// 		System.exit(0);
		// 	}
		// }catch(Exception e){
		// 	System.out.println("there was an error executing the sql statement against the filemaker system:");
		// 	System.out.println(e);
		// 	System.exit(0);
		// }
	}
}
