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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class RecordServiceImpl implements RecordService {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	@Autowired
	private RecordDao recordDao;
	
	@Override
	public String getRecords(String sessionId,String user_id,String tenant_id,String ani,String dnis,String token,int pageNo,int pageSize) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		JSONObject user = AuthUtil.instance.getUser(sessionId, user_id);
		if (null==user || user.isNullObject()) {
			throw new NotAcceptableException("没有权限");
		}
		JSONArray roles = user.getJSONArray("roles");
		JSONObject role = roles.getJSONObject(0);
		String role_name=role.getString("role_name");
		if (!"qc".equals(role_name) && !"tm".equals(role_name)) {//允许质检员和租户管理员查看质检记录
			throw new NotAcceptableException("角色异常");
		}
		List<Document> records =null;
		if (token.equals("crm")) {//使用crm系统的用户，只能查询属于自己数据
			records = recordDao.getRecords(user_id,"",ani,dnis,pageNo,pageSize);
		}else if(token.equals("tm")){//使用tm系统的用户，可以查询当前租户下所有数据
			records = recordDao.getRecords("",tenant_id,ani,dnis,pageNo,pageSize);
		}else{//其他token直接返回失败
			throw new NotAcceptableException("凭证异常");
		}
		responseMap.put("data", records);
		responseMap.put("count", records.size());
		responseMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return gson.toJson(responseMap);
	}
	
	@Override
	public String addRecord(String record,String sessionId,String user_id) throws NotAcceptableException {
		Map<String,Object> responseMap = new HashMap<String,Object>();
		String record_id = UUID.randomUUID().toString();
		Document recordDoc=null;
		try{
			JSONObject tempJson = JSONObject.fromObject(record);//这里面包含录音信息（即record）和质检结果(result)
			JSONObject recordJson = tempJson.getJSONObject("record");
			recordDoc = Document.parse(recordJson.toString());
			recordDoc.remove("id");
			recordDoc.put("_id", record_id);
			JSONObject user = AuthUtil.instance.getUser(sessionId, user_id);
			if (null==user || user.isNullObject()) {
				throw new NotAcceptableException("没有权限");
			}
			JSONArray roles = user.getJSONArray("roles");
			JSONObject role = roles.getJSONObject(0);
			String role_name=role.getString("role_name");
			if (!"qc".equals(role_name)) {
				throw new NotAcceptableException("角色异常");
			}
			recordDoc.put("qc_id",user_id);//user_id就是质检员
			recordDoc.put("qc_login_name", user.getString("login_name"));
			recordDoc.put("qc_username", user.getString("username"));
			recordDoc.put("qc_time", sdf.format(new Date(System.currentTimeMillis())));
			recordDoc.put("tenant_id", user.getString("tenant_id"));
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
