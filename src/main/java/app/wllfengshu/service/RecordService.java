package app.wllfengshu.service;

import app.wllfengshu.exception.NotAcceptableException;

public interface RecordService {
	
	public String getRecords(String sessionId,String user_id,String ani,String dnis,String token,int pageNo,int pageSize) throws NotAcceptableException;

	public String addRecord(String record,String sessionId,String user_id) throws NotAcceptableException;

	public String getRecord(String record_id,String sessionId,String user_id) throws NotAcceptableException;

	public String updateRecord(String record,String sessionId,String user_id) throws NotAcceptableException;

	public String deleteRecord(String record_id,String sessionId,String user_id) throws NotAcceptableException;
	
}
