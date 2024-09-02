package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.greenario.common.rest.models.CollectionRepresentationModel;

import java.beans.ConstructorProperties;
import java.util.Collection;

public class DistributionTargetsCollectionRepresentationModel
    extends CollectionRepresentationModel<CropConditionSummaryRepresentationModel> {

  @ConstructorProperties({ "distributionTargets" })
  public DistributionTargetsCollectionRepresentationModel(
      Collection<CropConditionSummaryRepresentationModel> content) {
    super(content);
  }

  /** the embedded distribution targets. */
  @JsonProperty("distributionTargets")
  @Override
  public Collection<CropConditionSummaryRepresentationModel> getContent() {
    return super.getContent();
  }
}
