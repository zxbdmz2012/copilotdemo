package com.github.copilot.user.model;

import com.github.copilot.user.feign.UserQueryInterface;
import com.github.copilot.util.ServiceProviderUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
@Builder
public class UserInfo {

    private String userId;

    private List<String> roles;


    public List<String> getRoles() {
        if(Objects.nonNull(roles)&&!roles.isEmpty()){
            return roles;
        }else {
            UserQueryInterface userQueryInterface = (UserQueryInterface) ServiceProviderUtil.getBean(UserQueryInterface.class);
            return userQueryInterface.getByUserId(userId).getRoles();
        }
    }
}
