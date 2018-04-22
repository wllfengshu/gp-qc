package app.wllfengshu.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.wllfengshu.dao.RecordDao;
import app.wllfengshu.exception.NotAcceptableException;
import app.wllfengshu.util.AuthUtil;
import net.sf.json.JSONObject;

@Service
public class RecordServiceImpl implements RecordService {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	@Autowired
	private RecordDao recordDao;
	
	@Override
	public String getRecords(String sessionId,String user_id,int pageNo,int pageSize) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		AuthUtil.instance.checkUserInfo(sessionId, user_id);
		List<Document> records = recordDao.getRecords(user_id,pageNo,pageSize);
		responseMap.put("data", records);
		responseMap.put("count", records.size());
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}
	
	@Override
	public String addRecord(String record,String sessionId,String user_id) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		String record_id = UUID.randomUUID().toString();
		AuthUtil.instance.checkUserInfo(sessionId, user_id);
		Document recordDoc =null;
		try{
			recordDoc = Document.parse(record);
			recordDoc.put("id", record_id);
			JSONObject user = AuthUtil.instance.getUser(sessionId, user_id);
			recordDoc.put("qc_id",user_id);//user_id就是质检员
			recordDoc.put("qc_login_name", user.getString("login_name"));
			recordDoc.put("qc_username", user.getString("username"));
			recordDoc.put("qc_time", sdf.format(new Date(System.currentTimeMillis())));
		}catch(Exception e){
			throw new NotAcceptableException("数据格式错误");
		}
		recordDao.addRecord(recordDoc);
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}
	
	@Override
	public String getRecord(String record_id,String sessionId,String user_id) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		AuthUtil.instance.checkUserInfo(sessionId, user_id);
		Document record = recordDao.getRecord(record_id);
		responseMap.put("data", record);
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}
	
	@Override
	public String updateRecord(String record,String sessionId,String user_id) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		AuthUtil.instance.checkUserInfo(sessionId, user_id);
		Document recordDoc =null;
		try{
			recordDoc = Document.parse(record);
		}catch(Exception e){
			throw new NotAcceptableException("数据格式错误");
		}
		recordDao.updateRecord(recordDoc);
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}
	
	@Override
	public String deleteRecord(String record_id,String sessionId,String user_id) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		AuthUtil.instance.checkUserInfo(sessionId, user_id);
		recordDao.deleteRecord(record_id);
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}

}
