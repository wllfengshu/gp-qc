package app.wllfengshu.dao;

import java.util.ArrayList;

import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

import app.wllfengshu.util.MongoDBUtil;

@Repository
public class RecordDaoImpl implements RecordDao{

	private MongoCollection<Document> col = MongoDBUtil.instance.getCollection(MongoDBUtil.defaultDB, "record");
	
	@Override
	public ArrayList<Document> getRecords(String user_id,int pageNo,int pageSize) {
		BasicDBObject condition = new BasicDBObject();
		condition.put("qc_id", new BasicDBObject("$eq",user_id));
		return col.find(condition).sort(new BasicDBObject("qc_time",-1)).skip((pageNo - 1) * pageSize).limit(pageSize).into(new ArrayList<Document>());
	}

	@Override
	public void addRecord(Document record) {
		col.insertOne(record);
	}

	@Override
	public Document getRecord(String id) {
		BasicDBObject condition = new BasicDBObject();
		condition.put("_id", new BasicDBObject("$eq",id));
		return col.find(condition).first();
	}

	@Override
	public void updateRecord(Document record) {
		BasicDBObject condition = new BasicDBObject();
		condition.put("_id", new BasicDBObject("$eq",record.get("id")));
		col.updateOne(condition, record);
	}

	@Override
	public void deleteRecord(String id) {
		BasicDBObject condition = new BasicDBObject();
		condition.put("_id", new BasicDBObject("$eq",id));
		col.deleteOne(condition);
	}
	
}
