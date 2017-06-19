package space.hehuoren.open.mybatis.generatorplus;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class GenMultiTableQuery {

	private JSONObject config;

	public static void main(String[] args) {

		try {
			new GenMultiTableQuery() {
				{
					initConfig();
					genMapper();
					genEntity();
					genMapperXml();
					genSqlProvider();
				}
			};
		} catch (IOException | TemplateException | SQLException e) {
			e.printStackTrace();
		}

	}

	public Connection connect() throws SQLException {
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		Connection connection = DriverManager
				.getConnection(
						"jdbc:mysql://127.0.0.1:3306/mitu?useUnicode=true&characterEncoding=utf8",
						"root", "");
		return connection;
	}

	public void genFile(Map<String, Object> ftlModel, String template,
			String outFile) throws IOException, TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
		cfg.setDirectoryForTemplateLoading(new File(this.getClass()
				.getClassLoader().getResource("ftl").getFile()));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		Template temp = cfg.getTemplate(template);
		Writer out = new OutputStreamWriter(
				FileUtils.openOutputStream(new File(config
						.getString("projectLocation"), outFile)));
		temp.process(ftlModel, out);
	}

	public void genEntity() throws IOException, TemplateException {
		JSONArray mappers = config.getJSONArray("mappers");
		for (Object mapper : mappers) {
			JSONObject m = (JSONObject) mapper;
			JSONArray methods = m.getJSONArray("methods");
			for (Object object : methods) {
				JSONObject json = (JSONObject) object;
				JSONObject entity = json.getJSONObject("returnEntity");
				JSONArray tables = entity.getJSONArray("tables");
				for (Object table : tables) {
					JSONObject t = (JSONObject) table;
					t.put("propertyName",
							tableNameToPropertyName(t.getString("name")));
				}
				StringBuffer outFile = new StringBuffer("src/main/java/")
						.append(StringUtils.replace(
								entity.getString("package"), ".", "/"))
						.append("/").append(entity.getString("className"))
						.append(".java");
				genFile(entity, "Entity.ftl", outFile.toString());
			}
		}

	}

	public void genMapper() throws IOException, TemplateException {
		JSONArray mappers = config.getJSONArray("mappers");
		for (Object mapper : mappers) {
			JSONObject m = (JSONObject) mapper;

			StringBuffer outFile = new StringBuffer("src/main/java/")
					.append(StringUtils.replace(config.getString("package"),
							".", "/")).append("/").append(m.getString("name"))
					.append(".java");
			m.put("package", config.getString("package"));
			genFile(m, "Mapper.java.ftl", outFile.toString());
		}

	}

	public void genMapperXml() throws IOException, TemplateException {
		JSONArray mappers = config.getJSONArray("mappers");
		for (Object mapper : mappers) {
			JSONObject m = (JSONObject) mapper;
			JSONArray methods = m.getJSONArray("methods");
			for (Object object : methods) {
				JSONObject json = (JSONObject) object;
				JSONObject entity = json.getJSONObject("returnEntity");
				JSONArray tables = entity.getJSONArray("tables");
				for (Object table : tables) {
					JSONObject t = (JSONObject) table;
					t.put("propertyName",
							tableNameToPropertyName(t.getString("name")));
				}

			}
			StringBuffer outFile = new StringBuffer(
					"src/main/resources/mybatis/").append(m.getString("name"))
					.append(".xml");
			m.put("package", config.getString("package"));
			genFile(m, "mapper.xml.ftl", outFile.toString());
		}

	}

	public void genSqlProvider() throws IOException, TemplateException,
			SQLException {
		JSONArray mappers = config.getJSONArray("mappers");
		for (Object mapper : mappers) {
			JSONObject m = (JSONObject) mapper;
			JSONArray methods = m.getJSONArray("methods");
			for (Object object : methods) {
				JSONObject method = (JSONObject) object;
				JSONObject entity = method.getJSONObject("returnEntity");
				JSONArray tables = entity.getJSONArray("tables");
				for (Object table : tables) {
					JSONObject t = (JSONObject) table;
					t.put("cols", getColumns(t.getString("name")));
				}
				method.put("primaryKey", primaryKeyColum(tables));

			}
			StringBuffer outFile = new StringBuffer("src/main/java/")
					.append(StringUtils.replace(config.getString("package"),
							".", "/")).append("/").append(m.getString("name"))
					.append("SqlProvider").append(".java");
			m.put("package", config.getString("package"));
			genFile(m, "SqlProvider.java.ftl", outFile.toString());
			genFile(m, "Condition.java.ftl",
					outFile.toString().replace("SqlProvider", "Condition"));
		}

	}

	private String primaryKeyColum(JSONArray tables) throws SQLException {
		Connection conn = connect();
		StringBuffer sb = new StringBuffer();
		for (Object table : tables) {
			JSONObject t = (JSONObject) table;
			ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null,
					t.getString("name"));
			while (rs.next()) {
				sb.append(",").append("ifnull(").append(t.getString("name")).append(".")
						.append(rs.getString("COLUMN_NAME")).append(",0)");
			}
		}
		sb.deleteCharAt(0);
		sb.append(") as id");
		sb.insert(0, "concat (");
		return sb.toString();
	}

	private static String tableNameToPropertyName(String table) {
		table = StringUtils.lowerCase(table);
		String[] s = table.split("_");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			if (i == 0) {
				sb.append(s[i]);
			} else {
				sb.append(Character.toUpperCase(s[i].charAt(0))).append(
						s[i].substring(1));
			}
		}
		return sb.toString();
	}

	public String getColumns(String table) throws SQLException {
		Connection conn = connect();
		StringBuffer sb = new StringBuffer();
		Statement stm = conn.createStatement();
		ResultSet rs = stm.executeQuery("select * from " + table);
		ResultSetMetaData meta = rs.getMetaData();
		int n = meta.getColumnCount();
		for (int i = 1; i <= n; i++) {
			sb.append(",").append(table).append(".")
					.append(meta.getColumnName(i)).append(" as ").append(table)
					.append("_").append(meta.getColumnName(i));
		}
		sb.deleteCharAt(0);
		return sb.toString();

	}

	public void initConfig() throws IOException {
		JSONObject json = JSON
				.parseObject(FileUtils
						.readFileToString(new File(
								"/Users/yanzhanghai/Documents/dev/mitu.workspace/generatorplus/src/main/resources/config.json")));
		this.config = json;

	}
}
