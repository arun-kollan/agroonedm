package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.models.PageMetadata;
import com.greenario.common.rest.models.PagedRepresentationModel;

import java.beans.ConstructorProperties;
import java.util.Collection;

public class CropConditionAccessItemPagedRepresentationModel
    extends PagedRepresentationModel<CropConditionAccessItemRepresentationModel> {

  @ConstructorProperties({ "accessItems", "page" })
  public CropConditionAccessItemPagedRepresentationModel(
      Collection<CropConditionAccessItemRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded access items. */
  @JsonProperty("accessItems")
  @Override
  public Collection<CropConditionAccessItemRepresentationModel> getContent() {
    return super.getContent();
  }
}
