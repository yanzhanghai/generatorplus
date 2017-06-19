package ${package};

<#list tables as table>
import ${table.modelPackage}.${table.modelClass};
</#list>

public class ${className} {
	private String id;
	<#list tables as table>
	private ${table.modelClass} ${table.propertyName};
	</#list>
	
	public String getId(){
		return this.id;
	}
	
	public void setId(String id){
		this.id=id;
	}
		
<#list tables as table>
	public ${table.modelClass} get${table.modelClass}() {
		return ${table.propertyName};
	}

	public void set${table.modelClass}(${table.modelClass} ${table.propertyName}) {
		this.${table.propertyName} = ${table.propertyName};
	}
</#list>
}
