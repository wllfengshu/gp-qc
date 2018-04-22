package app.wllfengshu.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



//import org.apache.commons.configuration.CompositeConfiguration;
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.PropertiesConfiguration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;


/**
 * MongoDB工具类 Mongo实例代表了一个数据库连接池，即使在多线程的环境中，一个Mongo实例对我们来说已经足够了<br>
 * 注意Mongo已经实现了连接池，并且是线程安全的。 <br>
 * 设计为单例模式， 因 MongoDB的Java驱动是线程安全的，对于一般的应用，只要一个Mongo实例即可，<br>
 * Mongo有个内置的连接池（默认为10个） 对于有大量写和读的环境中，为了确保在一个Session中使用同一个DB时，<br>
 * DB和DBCollection是绝对线程安全的<br>
 * 
 */
public enum MongoDBUtil {

    /**
     * 定义一个枚举的元素，它代表此类的一个实例
     */
    instance;

    private MongoClient mongoClient;

    public static String host="127.0.0.1";
    public static String port="27017";
    public static String defaultDB="saas";
    public static String user="admin";
    public static String password="";
    public static String authType="authless";
    private static final String AUTHLESS="authless";
    private static final String AUTH="auth";
    static {
		long beginTime=System.currentTimeMillis();
        LogUtils.trace(MongoDBUtil.class,"===============MongoDBUtil初始化========================");
        
        if(AUTH.equals(authType)||!AUTHLESS.equals(authType)){
			instance.mongoClient = new MongoClient(Arrays.asList(new ServerAddress(host,Integer.valueOf(port))), 
					Arrays.asList(MongoCredential.createScramSha1Credential(user, "admin",password.toCharArray())));
        }else if(AUTHLESS.equals(authType)){
        	MongoClientURI uri = new MongoClientURI("mongodb://"+host+":"+port+"/",
                    MongoClientOptions.builder().cursorFinalizerEnabled(false));
        	instance.mongoClient = new MongoClient(uri);
        }
        //init search
        MongoDatabase db = instance.mongoClient.getDatabase("admin");
		MongoCollection<Document> collection = db.getCollection("system.users");
		collection.find().into(new ArrayList<Document>());
		
	    Builder options = new MongoClientOptions.Builder();
	    options.connectionsPerHost(50);
//	    options.connectionsPerHost(300);// 连接池设置为300个连接,默认为100
	    options.connectTimeout(15000);// 连接超时，推荐>3000毫秒
	    options.maxWaitTime(5000); //
	    options.socketTimeout(0);// 套接字超时时间，0无限制
	    options.threadsAllowedToBlockForConnectionMultiplier(5000);// 线程队列数，如果连接线程排满了队列就会抛出“Out of semaphores to get db”错误。
	//    options.writeConcern(WriteConcern.SAFE);//
	    options.build();
	    long endTime=System.currentTimeMillis();
        System.out.println("MongoDBUtil init useTime: "+(endTime-beginTime));
    }

    // ------------------------------------共用方法---------------------------------------------------
    /**
     * 获取DB实例 - 指定DB
     * 
     * @param dbName
     * @return
     */
    public MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }

    /**
     * 获取collection对象 - 指定Collection
     * 
     * @param collName
     * @return
     */
    public MongoCollection<Document> getCollection(String dbName, String collName) {
        if (null == collName || "".equals(collName)) {
            return null;
        }
        if (null == dbName || "".equals(dbName)) {
            return null;
        }
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collName);
        return collection;
    }

    /**
     * 查询DB下的所有表名
     */
    public List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = getDB(dbName).listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * 获取所有数据库名称列表
     * 
     * @return
     */
    public MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * 删除一个数据库
     */
    public void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    /**
     * 查找对象 - 根据主键_id
     * 
     * @param collection
     * @param id
     * @return
     */
    public Document findById(MongoCollection<Document> coll, String id) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Document myDoc = coll.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /** 统计数 */
    public int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }

    /** 条件查询 */
    public MongoCursor<Document> find(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }

    /** 分页查询 */
    public MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return coll.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }

    /**
     * 通过ID删除
     * 
     * @param coll
     * @param id
     * @return
     */
    public int deleteById(MongoCollection<Document> coll, String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }

    public Document updateById(MongoCollection<Document> coll, String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        coll.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }

    public void dropCollection(String dbName, String collName) {
        getDB(dbName).getCollection(collName).drop();
    }

    /**
     * 关闭Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * 测试入口
     * 
     * @param args
     */
    public static void main(String[] args) {
        String dbName = "GC_MAP_DISPLAY_DB";
        String collName = "COMMUNITY_BJ";
        MongoCollection<Document> coll = MongoDBUtil.instance.getCollection(dbName, collName);
        Bson filter = Filters.eq("count", 0);
        MongoDBUtil.instance.find(coll, filter);

    }

}