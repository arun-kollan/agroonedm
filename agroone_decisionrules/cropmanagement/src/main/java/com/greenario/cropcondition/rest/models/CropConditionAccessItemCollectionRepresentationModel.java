package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.models.CollectionRepresentationModel;

import java.beans.ConstructorProperties;
import java.util.Collection;

public class CropConditionAccessItemCollectionRepresentationModel
    extends CollectionRepresentationModel<CropConditionAccessItemRepresentationModel> {

  @ConstructorProperties("accessItems")
  public CropConditionAccessItemCollectionRepresentationModel(
      Collection<CropConditionAccessItemRepresentationModel> content) {
    super(content);
  }

  /** the embedded access items. */
  @JsonProperty("accessItems")
  @Override
  public Collection<CropConditionAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
