package com.greenario.common.rest.models;

import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

import com.greenario.common.api.GreenarioRole;

/** EntityModel class for user information. */
public class GreenarioUserInfoRepresentationModel
    extends RepresentationModel<GreenarioUserInfoRepresentationModel> {

  /** The user Id of the current user. */
  private String userId;
  /** All groups the current user is a member of. */
  private List<String> groupIds = new ArrayList<>();
  /** All greenario roles the current user fulfills. */
  private List<GreenarioRole> roles = new ArrayList<>();

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public List<String> getGroupIds() {
    return groupIds;
  }

  public void setGroupIds(List<String> groupIds) {
    this.groupIds = groupIds;
  }

  public List<GreenarioRole> getRoles() {
    return roles;
  }

  public void setRoles(List<GreenarioRole> roles) {
    this.roles = roles;
  }

  @Override
  public @NonNull String toString() {
    return "GreenarioUserInfoRepresentationModel [userId="
        + userId
        + ", groupIds="
        + groupIds
        + ", roles="
        + roles
        + "]";
  }
}
