package ${package};

<#list tables as table>
import ${table.modelPackage}.${table.modelClass};
</#list>

public class ${className} {
	<#list tables as table>
	private ${table.modelClass} ${table.propertyName};
	</#list>
	
<#list tables as table>
	public ${table.modelClass} get${table.modelClass}() {
		return ${table.propertyName};
	}

	public void set${table.modelClass}(${table.modelClass} ${table.propertyName}) {
		this.${table.propertyName} = ${table.propertyName};
	}
</#list>
}
