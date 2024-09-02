package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.models.CollectionRepresentationModel;

import java.beans.ConstructorProperties;
import java.util.Collection;

public class CropConditionDefinitionCollectionRepresentationModel
    extends CollectionRepresentationModel<CropConditionDefinitionRepresentationModel> {

  @ConstructorProperties("cropConditionDefinitions")
  public CropConditionDefinitionCollectionRepresentationModel(
      Collection<CropConditionDefinitionRepresentationModel> content) {
    super(content);
  }

  /** the embedded cropCondition definitions. */
  @JsonProperty("cropConditionDefinitions")
  @Override
  public Collection<CropConditionDefinitionRepresentationModel> getContent() {
    return super.getContent();
  }
}
