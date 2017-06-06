package ${package};

import org.apache.ibatis.jdbc.SQL;

public class ${name}SqlProvider {
	<#list methods as method>
	public String ${method.name}(${name}Condition condition){
		return new SQL(){{
			<#assign entity=method.returnEntity>
  			<#assign tables=entity.tables>
  			<#list tables as table>
			SELECT("${table.cols!}");
			</#list>
			<#list tables as table>
			FROM("${table.name}");
			</#list>
			WHERE("${entity.join}");
		}}.toString();
	}
	</#list>
}