@echo:
@echo ==== userApi.wsdl ====
curl http://127.0.0.1:8080/ws/wsdl/userApi.wsdl
@echo:
@echo:
@echo ==== UserReadResponse ====
curl -fsSL --header "content-type: text/xml" -d @curlSOAP.xml http://127.0.0.1:8080/ws
