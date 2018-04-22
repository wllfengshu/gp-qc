package app.wllfengshu.entity;
/**
 * @title 质检记录，存放在mongodb中
 */
public class Record {
	private String id;
	private String ani;
	private String call_direction;
	private String call_id;
	private double call_length;
	private String dnis;
	private String end_time;
	private String file_place;
	private double file_size;
	private String start_time;
	private int state;
	private String agent_id;//坐席ID
	private String agent_login_name;
	private String agent_username;
	private String qc_id;//质检员ID
	private String qc_login_name;
	private String qc_username;
	private String result;//质检结果
	private String qc_time;//质检时间
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAni() {
		return ani;
	}
	public void setAni(String ani) {
		this.ani = ani;
	}
	public String getCall_direction() {
		return call_direction;
	}
	public void setCall_direction(String call_direction) {
		this.call_direction = call_direction;
	}
	public String getCall_id() {
		return call_id;
	}
	public void setCall_id(String call_id) {
		this.call_id = call_id;
	}
	public double getCall_length() {
		return call_length;
	}
	public void setCall_length(double call_length) {
		this.call_length = call_length;
	}
	public String getDnis() {
		return dnis;
	}
	public void setDnis(String dnis) {
		this.dnis = dnis;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getFile_place() {
		return file_place;
	}
	public void setFile_place(String file_place) {
		this.file_place = file_place;
	}
	public double getFile_size() {
		return file_size;
	}
	public void setFile_size(double file_size) {
		this.file_size = file_size;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getAgent_id() {
		return agent_id;
	}
	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}
	public String getAgent_login_name() {
		return agent_login_name;
	}
	public void setAgent_login_name(String agent_login_name) {
		this.agent_login_name = agent_login_name;
	}
	public String getAgent_username() {
		return agent_username;
	}
	public void setAgent_username(String agent_username) {
		this.agent_username = agent_username;
	}
	public String getQc_id() {
		return qc_id;
	}
	public void setQc_id(String qc_id) {
		this.qc_id = qc_id;
	}
	public String getQc_login_name() {
		return qc_login_name;
	}
	public void setQc_login_name(String qc_login_name) {
		this.qc_login_name = qc_login_name;
	}
	public String getQc_username() {
		return qc_username;
	}
	public void setQc_username(String qc_username) {
		this.qc_username = qc_username;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getQc_time() {
		return qc_time;
	}
	public void setQc_time(String qc_time) {
		this.qc_time = qc_time;
	}

}
