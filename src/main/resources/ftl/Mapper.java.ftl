package ${package};

import java.util.List;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.SelectProvider;

<#list methods as method>
import ${method.returnEntity.package}.${method.returnEntity.className};
</#list>
public interface ${name} {
	<#list methods as method>
	@ResultMap("${package}.${method.returnEntity.className}")
	@SelectProvider(type = ${package}.${name}SqlProvider.class, method = "${method.name}")
	List<${method.returnEntity.className}> ${method.name}(${name}Condition condition);
	</#list>
}