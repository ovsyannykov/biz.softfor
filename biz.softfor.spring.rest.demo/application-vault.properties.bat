set appName=crib
set cfgName=spring.datasource
vault kv put secret/%appName%^
  %cfgName%.host=mem^
  %cfgName%.port=^
  %cfgName%.name=demodb^
  %cfgName%.url=jdbc:h2:mem:demodb^
  %cfgName%.username=sa^
  %cfgName%.password=^
  %cfgName%.driverClassName=org.h2.Driver
