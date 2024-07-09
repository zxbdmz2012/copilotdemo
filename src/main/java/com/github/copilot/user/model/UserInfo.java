package com.github.copilot.user.model;

import lombok.*;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true)
@Builder
public class UserInfo {

    private String userId;

    private List<String> roles;

}
