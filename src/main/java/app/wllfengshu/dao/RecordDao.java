package app.wllfengshu.dao;

import java.util.ArrayList;

import org.bson.Document;

public interface RecordDao {
	public ArrayList<Document> getRecords(String user_id, int pageNo, int pageSize);

	public void addRecord(Document record);

	public Document getRecord(String id);
	
	public void updateRecord(Document record);

	public void deleteRecord(String id);
	
}
