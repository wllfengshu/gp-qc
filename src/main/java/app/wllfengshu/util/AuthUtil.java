package app.wllfengshu.util;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import app.wllfengshu.exception.NotAcceptableException;
import net.sf.json.JSONObject;

public enum AuthUtil {
	/**
	 * 权限认证
	 */
	instance;
	
	/**
	 * @throws NotAcceptableException 
	 * @title 检查user信息
	 */
	public void checkUserInfo(String sessionId,String user_id) throws NotAcceptableException{
		Object userAllInfo = getUsersAllInfo(sessionId,user_id);
		if (null==userAllInfo || "".equals(userAllInfo)) {
			throw new NotAcceptableException("没有权限");
		}
	}
	
	/**
	 * @titile 获取用户信息
	 */
	public JSONObject getUser(String sessionId,String user_id){
		Object usersAllInfo = getUsersAllInfo(sessionId, user_id);
		JSONObject userAllInfoJson = JSONObject.fromObject(usersAllInfo);
		return userAllInfoJson;
	}
	
	/**
	 * @title 获取user所有信息
	 */
	private Object getUsersAllInfo(String sessionId,String user_id){
		long beginTime=System.currentTimeMillis();
		String security_base_url="http://127.0.0.1:9001/";
		String url = security_base_url+"security/user/"+user_id;
		Response res=null;
		try {
			res = Request.Get(url).setHeader("sessionId", sessionId).execute();
			HttpResponse response = res.returnResponse();
			String responseStr= null;
			if(response.getEntity()!=null){
				responseStr = IOUtils.toString(response.getEntity().getContent(),"utf-8");
			}
			long endTime=System.currentTimeMillis();
			int statusCode = response.getStatusLine().getStatusCode();
			String logStr="{\"statusCode\":"+statusCode+",\"useTime\":\""+(endTime-beginTime)+"ms\",\"url\":\""+url+"\",\"serviceResponseStr\":"+responseStr+"}";
			LogUtils.trace(this, "########## AuthUtil.getAgentByNo,%s ##########",logStr);
			if(statusCode>300){
				throw new SecurityException(logStr);
			}
			JSONObject userInfo = JSONObject.fromObject(responseStr);
			return userInfo.get("data");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
	
