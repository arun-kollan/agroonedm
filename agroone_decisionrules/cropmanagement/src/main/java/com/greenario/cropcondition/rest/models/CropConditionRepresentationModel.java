package com.greenario.cropcondition.rest.models;

import java.time.Instant;

import com.greenario.cropcondition.api.models.CropCondition;

/** EntityModel class for {@link CropCondition}. */
public class CropConditionRepresentationModel extends CropConditionSummaryRepresentationModel {

  /**
   * The creation timestamp of the cropCondition in the system.
   *
   * <p>
   * The format is ISO-8601.
   */
  private Instant created;
  /**
   * The timestamp of the last modification.
   *
   * <p>
   * The format is ISO-8601.
   */
  private Instant modified;

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }
}
