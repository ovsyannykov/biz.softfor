package biz.softfor.user.api;

import biz.softfor.util.Json;
import biz.softfor.util.api.BasicResponse;
import lombok.extern.java.Log;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

@Log
public class DeserelizationTest {

  @Test
  public void manyToMany() throws Exception {
    String body = """
    {
        "status": 0,
        "descr": "OK",
        "data": [
            {
                "id": 1,
                "username": "testMgr",
                "email": "testMgr@t.co",
                "roles": [
                    {
                        "id": 2,
                        "group": {
                            "id": 1,
                            "name": "Tezzt"
                        },
                        "actions": [
                            {
                                "id": -5661975479719594045,
                                "defaultAccess": 0,
                                "typ": 1,
                                "name": "Roles (update)",
                                "match": "biz.softfor.user.jpa.Role",
                                "objName": "biz.softfor.user.jpa.Role,cu",
                                "description": "Roles (update)"
                            },
                            {
                                "id": 2420746868417822974,
                                "defaultAccess": 0,
                                "typ": 2,
                                "name": "Person details (update)",
                                "match": "personDetails",
                                "objName": "biz.softfor.partner.jpa.Partner.personDetails,fu",
                                "description": "Person details (update)"
                            }
                        ]
                    }
                ]
            }
        ],
        "total": 0
    }""";
    UserResponse res = Json.objectMapper().readValue(body, UserResponse.class);
    assertThat(res.getStatus()).as("body").isEqualTo(BasicResponse.OK);
  }

}
