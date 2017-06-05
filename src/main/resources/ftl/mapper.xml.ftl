<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${package}">
  <#list methods as method>
  <#assign entity=method.returnEntity>
  <#assign tables=entity.tables>
  <resultMap id="${entity.className}" type="${entity.package}.${entity.className}">
  	<#list tables as table>
    <association property="${table.propertyName}" javaType="${table.modelPackage}.${table.modelClass}" resultMap="${table.modelMap}" columnPrefix="${table.propertyName}_"/>
    </#list>
  </resultMap>
  </#list>
</mapper>