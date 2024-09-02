package com.greenario.cropcondition.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.QueryParameter;
import com.greenario.cropcondition.api.CropConditionPermission;
import com.greenario.cropcondition.api.CropConditionQuery;
import com.greenario.cropcondition.api.CropConditionType;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class CropConditionQueryFilterParameter implements QueryParameter<CropConditionQuery, Void> {

  /** Filter by the name of the CropCondition. This is an exact match. */
  @JsonProperty("name")
  private final String[] name;

  /**
   * Filter by the name of the CropCondition. This results in a substring search.
   * (%
   * is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be
   * resolved correctly.
   */
  @JsonProperty("name-like")
  private final String[] nameLike;

  /** Filter by the key of the CropCondition. This is an exact match. */
  @JsonProperty("key")
  private final String[] key;

  /**
   * Filter by the key of the CropCondition. This results in a substring search..
   * (%
   * is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be
   * resolved correctly.
   */
  @JsonProperty("key-like")
  private final String[] keyLike;

  /** Filter by the owner of the CropCondition. This is an exact match. */
  @JsonProperty("owner")
  private final String[] owner;

  /**
   * Filter by the owner of the CropCondition. This results in a substring
   * search..
   * (% is appended to
   * the beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be
   * resolved correctly.
   */
  @JsonProperty("owner-like")
  private final String[] ownerLike;

  /**
   * Filter by the description of the CropCondition. This results in a substring
   * search.. (% is
   * appended to the beginning and end of the requested value). Further SQL "LIKE"
   * wildcard
   * characters will be resolved correctly.
   */
  @JsonProperty("description-like")
  private final String[] descriptionLike;

  /** Filter by the domain of the CropCondition. This is an exact match. */
  @JsonProperty("domain")
  private final String[] domain;

  /** Filter by the type of the CropCondition. This is an exact match. */
  @JsonProperty("type")
  private final CropConditionType[] type;

  /** Filter by the required permission for the CropCondition. */
  @JsonProperty("required-permission")
  private final CropConditionPermission[] requiredPermissions;

  @SuppressWarnings("indentation")
  @ConstructorProperties({
      "name",
      "name-like",
      "key",
      "key-like",
      "owner",
      "owner-like",
      "description-like",
      "domain",
      "type",
      "required-permission"
  })
  public CropConditionQueryFilterParameter(
      String[] name,
      String[] nameLike,
      String[] key,
      String[] keyLike,
      String[] owner,
      String[] ownerLike,
      String[] descriptionLike,
      String[] domain,
      CropConditionType[] type,
      CropConditionPermission[] requiredPermissions) {
    this.name = name;
    this.nameLike = nameLike;
    this.key = key;
    this.keyLike = keyLike;
    this.owner = owner;
    this.ownerLike = ownerLike;
    this.descriptionLike = descriptionLike;
    this.domain = domain;
    this.type = type;
    this.requiredPermissions = requiredPermissions;
  }

  @Override
  public Void apply(CropConditionQuery query) {
    Optional.ofNullable(name).ifPresent(query::nameIn);
    Optional.ofNullable(nameLike).map(this::wrapElementsInLikeStatement).ifPresent(query::nameLike);
    Optional.ofNullable(key).ifPresent(query::keyIn);
    Optional.ofNullable(keyLike).map(this::wrapElementsInLikeStatement).ifPresent(query::keyLike);
    Optional.ofNullable(owner).ifPresent(query::ownerIn);
    Optional.ofNullable(ownerLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::ownerLike);
    Optional.ofNullable(descriptionLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::descriptionLike);
    Optional.ofNullable(domain).ifPresent(query::domainIn);
    Optional.ofNullable(type).ifPresent(query::typeIn);
    Optional.ofNullable(requiredPermissions).ifPresent(query::callerHasPermissions);
    return null;
  }
}
