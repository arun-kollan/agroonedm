package com.greenario.cropcondition.rest;

import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenario.common.api.BaseQuery.SortDirection;
import com.greenario.common.api.exceptions.InvalidArgumentException;
import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.rest.QueryPagingParameter;
import com.greenario.common.rest.QuerySortBy;
import com.greenario.common.rest.QuerySortParameter;
import com.greenario.common.rest.RestEndpoints;
import com.greenario.common.rest.ldap.LdapClient;
import com.greenario.common.rest.util.QueryParamsValidator;
import com.greenario.cropcondition.api.CropConditionAccessItemQuery;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.models.CropConditionAccessItem;
import com.greenario.cropcondition.rest.assembler.CropConditionAccessItemRepresentationModelAssembler;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemPagedRepresentationModel;

/** Controller for CropCondition access. */
@RestController
@Component
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class CropConditionAccessItemController {

  private final LdapClient ldapClient;
  private final CropConditionService cropConditionService;
  private final CropConditionAccessItemRepresentationModelAssembler modelAssembler;

  @Autowired
  public CropConditionAccessItemController(
      LdapClient ldapClient,
      CropConditionService cropConditionService,
      CropConditionAccessItemRepresentationModelAssembler modelAssembler) {
    this.ldapClient = ldapClient;
    this.cropConditionService = cropConditionService;
    this.modelAssembler = modelAssembler;
  }

  /**
   * This endpoint retrieves a list of existing CropCondition Access Items.
   * Filters
   * can be applied.
   *
   * @title Get a list of all CropCondition Access Items
   * @param request         the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter   the sort parameters
   * @param pagingParameter the paging parameters
   * @return the CropCondition Access Items with the given filter, sort and paging
   *         options.
   * @throws NotAuthorizedException if the user is not authorized.
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION_ACCESS_ITEMS)
  public ResponseEntity<CropConditionAccessItemPagedRepresentationModel> getCropConditionAccessItems(
      HttpServletRequest request,
      CropConditionAccessItemQueryFilterParameter filterParameter,
      CropConditionAccessItemQuerySortParameter sortParameter,
      QueryPagingParameter<CropConditionAccessItem, CropConditionAccessItemQuery> pagingParameter)
      throws NotAuthorizedException {

    QueryParamsValidator.validateParams(
        request,
        CropConditionAccessItemQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    CropConditionAccessItemQuery query = cropConditionService.createCropConditionAccessItemQuery();
    filterParameter.apply(query);
    sortParameter.apply(query);

    List<CropConditionAccessItem> cropConditionAccessItems = pagingParameter.apply(query);

    CropConditionAccessItemPagedRepresentationModel pagedResources = modelAssembler.toPagedModel(
        cropConditionAccessItems,
        pagingParameter.getPageMetadata());

    return ResponseEntity.ok(pagedResources);
  }

  /**
   * This endpoint deletes all CropCondition Access Items for a provided Access
   * Id.
   *
   * @title Delete a CropCondition Access Item
   * @param accessId the Access Id whose CropCondition Access Items should be
   *                 removed
   * @return no content
   * @throws NotAuthorizedException   if the user is not authorized.
   * @throws InvalidArgumentException if some argument is invalid.
   */
  @DeleteMapping(path = RestEndpoints.URL_CROPCONDITION_ACCESS_ITEMS)
  public ResponseEntity<Void> removeCropConditionAccessItems(
      @RequestParam("access-id") String accessId)
      throws NotAuthorizedException, InvalidArgumentException {
    if (ldapClient.isUser(accessId)) {
      List<CropConditionAccessItem> cropConditionAccessItemList = cropConditionService
          .createCropConditionAccessItemQuery()
          .accessIdIn(accessId).list();

      if (cropConditionAccessItemList != null && !cropConditionAccessItemList.isEmpty()) {
        cropConditionService.deleteCropConditionAccessItemsForAccessId(accessId);
      }
    } else {
      throw new InvalidArgumentException(
          String.format(
              "AccessId '%s' is not a user. " + "You can remove all access items for users only.",
              accessId));
    }

    return ResponseEntity.noContent().build();
  }

  public enum CropConditionAccessItemSortBy implements QuerySortBy<CropConditionAccessItemQuery> {
    CROPCONDITION_KEY(CropConditionAccessItemQuery::orderByCropConditionKey),
    ACCESS_ID(CropConditionAccessItemQuery::orderByAccessId);

    private final BiConsumer<CropConditionAccessItemQuery, SortDirection> consumer;

    CropConditionAccessItemSortBy(BiConsumer<CropConditionAccessItemQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(CropConditionAccessItemQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the
  // generic 'sort-by'
  // parameter from the super class.
  public static class CropConditionAccessItemQuerySortParameter
      extends QuerySortParameter<CropConditionAccessItemQuery, CropConditionAccessItemSortBy> {

    @ConstructorProperties({ "sort-by", "order" })
    public CropConditionAccessItemQuerySortParameter(
        List<CropConditionAccessItemSortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<CropConditionAccessItemSortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
