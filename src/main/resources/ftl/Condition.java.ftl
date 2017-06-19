package ${package};

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.jdbc.SQL;

public class ${name}Condition extends HashMap<String, Object>{

	private static final long serialVersionUID = 1L;
	private SQL sql;
	public ${name}Condition() {
		this.sql=new SQL();
	}
	public SQL where(String condition){
		return sql.WHERE(condition);
	}
	public SQL where(String condition,Object o){
		Pattern pattern = Pattern.compile("(?<=\\{)[\\S\\s]*(?=\\})");
		Matcher matcher = pattern.matcher(condition);
		if(matcher.find()){
			put(matcher.group(),o);
		}
		return sql.WHERE(condition);
	}
	public SQL and(){
		return sql.AND();
	}
	public SQL or(){
		return sql.OR();
	}
	public SQL orderBy(String columns){
		return sql.ORDER_BY(columns);
	}

	public SQL getSql(){
		return sql;
	}
	public void setParameter(String name,Object value){
		this.put(name,value);
	}
}
