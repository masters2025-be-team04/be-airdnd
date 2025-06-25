package rice_monkey.member.oauth.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String,Object> attribute;

    public NaverResponse(Map<String,Object> attributes){
        this.attribute=(Map<String,Object>)attributes.get("response");
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
