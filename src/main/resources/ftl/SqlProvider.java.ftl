package ${package};

import org.apache.ibatis.jdbc.SQL;

public class ${name}SqlProvider {
	<#list methods as method>
	public String ${method.name}(${name}Condition condition){
		SQL sql=condition==null?(new SQL()):condition.getSql();
		<#assign entity=method.returnEntity>
  		<#assign tables=entity.tables>
			<#assign joins=entity.joins>
  		<#list tables as table>
		sql.SELECT("${table.cols!}");
		</#list>
		sql.FROM("${tables[0].name}");
		<#list joins as join>
			<#if join.type == "left">
				sql.LEFT_OUTER_JOIN("${join.condition}");
			<#elseif join.type == "right">
				sql.RIGHT_OUTER_JOIN("${join.condition}");
			<#else>
				sql.JOIN("${join.condition}");
			</#if>
		</#list>
		sql.SELECT("${method.primaryKey}");
		return sql.toString();
	}
	</#list>
}
