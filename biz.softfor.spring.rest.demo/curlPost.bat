rem curl -d "{\"filter\":{\"id\":[51]},\"fields\":[\"username\",\"email\",\"personId\",\"roles.name\",\"roles.group.name\"]}" -H "Content-Type: application/json;charset=UTF-8" -X POST http://localhost:8080/user/read
curl -d "{\"fields\":[\"partnerName\",\"users\"]}" -H "Content-Type: application/json;charset=UTF-8" -X POST http://localhost:8080/partner/read
