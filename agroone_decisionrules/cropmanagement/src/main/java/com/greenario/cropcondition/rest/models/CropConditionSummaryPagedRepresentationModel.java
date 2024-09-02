package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.models.PageMetadata;
import com.greenario.common.rest.models.PagedRepresentationModel;

import java.beans.ConstructorProperties;
import java.util.Collection;

public class CropConditionSummaryPagedRepresentationModel
    extends PagedRepresentationModel<CropConditionSummaryRepresentationModel> {

  @ConstructorProperties({ "cropConditions", "page" })
  public CropConditionSummaryPagedRepresentationModel(
      Collection<CropConditionSummaryRepresentationModel> content, PageMetadata pageMetadata) {
    super(content, pageMetadata);
  }

  /** the embedded cropConditions. */
  @JsonProperty("cropConditions")
  @Override
  public Collection<CropConditionSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
