package com.greenario.cropcondition.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.QueryParameter;
import com.greenario.cropcondition.api.CropConditionAccessItemQuery;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class CropConditionAccessItemQueryFilterParameter
    implements QueryParameter<CropConditionAccessItemQuery, Void> {

  /** Filter by the key of the CropCondition. This is an exact match. */
  @JsonProperty("cropCondition-key")
  private final String[] cropConditionKey;

  /**
   * Filter by the key of the CropCondition. This results in a substring search..
   * (%
   * is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be
   * resolved correctly.
   */
  @JsonProperty("cropCondition-key-like")
  private final String[] cropConditionKeyLike;

  /** Filter by the name of the access id. This is an exact match. */
  @JsonProperty("access-id")
  private final String[] accessId;

  /**
   * Filter by the name of the access id. This results in a substring search.. (%
   * is appended to the
   * beginning and end of the requested value). Further SQL "LIKE" wildcard
   * characters will be
   * resolved correctly.
   */
  @JsonProperty("access-id-like")
  private final String[] accessIdLike;

  @ConstructorProperties({ "cropCondition-key", "cropCondition-key-like", "access-id", "access-id-like" })
  public CropConditionAccessItemQueryFilterParameter(
      String[] cropConditionKey,
      String[] cropConditionKeyLike,
      String[] accessId,
      String[] accessIdLike) {
    this.cropConditionKey = cropConditionKey;
    this.cropConditionKeyLike = cropConditionKeyLike;
    this.accessId = accessId;
    this.accessIdLike = accessIdLike;
  }

  @Override
  public Void apply(CropConditionAccessItemQuery query) {
    Optional.ofNullable(cropConditionKey).ifPresent(query::cropConditionKeyIn);
    Optional.ofNullable(cropConditionKeyLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::cropConditionKeyLike);
    Optional.ofNullable(accessId).ifPresent(query::accessIdIn);
    Optional.ofNullable(accessIdLike)
        .map(this::wrapElementsInLikeStatement)
        .ifPresent(query::accessIdLike);
    return null;
  }
}
