package com.greenario.cropcondition.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;

/**
 * this class represents a cropCondition including its distro targets and
 * authorisations.
 */
public class CropConditionDefinitionRepresentationModel
    extends RepresentationModel<CropConditionDefinitionRepresentationModel> {

  /** The cropCondition which is represented. */
  @JsonIgnoreProperties("_links")
  private CropConditionRepresentationModel cropCondition;
  /** The cropCondition authorizations. */
  private Collection<CropConditionAccessItemRepresentationModel> authorizations = new ArrayList<>();
  /** The distribution targets for this cropCondition. */
  private Set<String> distributionTargets = new HashSet<>();

  public Set<String> getDistributionTargets() {
    return distributionTargets;
  }

  public void setDistributionTargets(Set<String> distributionTargets) {
    this.distributionTargets = distributionTargets;
  }

  public Collection<CropConditionAccessItemRepresentationModel> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(
      Collection<CropConditionAccessItemRepresentationModel> authorizations) {
    this.authorizations = authorizations;
  }

  public CropConditionRepresentationModel getCropCondition() {
    return cropCondition;
  }

  public void setCropCondition(CropConditionRepresentationModel cropCondition) {
    this.cropCondition = cropCondition;
  }
}
