package com.greenario.common.rest.models;

import java.util.Collection;

public class CollectionRepresentationModel<T> {

  private final Collection<T> content;

  public CollectionRepresentationModel(Collection<T> content) {
    this.content = content;
  }

  public Collection<T> getContent() {
    return content;
  }
}
