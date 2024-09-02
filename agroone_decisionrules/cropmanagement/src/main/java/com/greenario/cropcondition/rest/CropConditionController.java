package com.greenario.cropcondition.rest;

import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.greenario.common.api.BaseQuery.SortDirection;
import com.greenario.common.api.exceptions.ConcurrencyException;
import com.greenario.common.api.exceptions.DomainNotFoundException;
import com.greenario.common.api.exceptions.InvalidArgumentException;
import com.greenario.common.api.exceptions.NotAuthorizedException;
import com.greenario.common.rest.QueryPagingParameter;
import com.greenario.common.rest.QuerySortBy;
import com.greenario.common.rest.QuerySortParameter;
import com.greenario.common.rest.RestEndpoints;
import com.greenario.common.rest.util.QueryParamsValidator;
import com.greenario.cropcondition.api.CropConditionCustomField;
import com.greenario.cropcondition.api.CropConditionQuery;
import com.greenario.cropcondition.api.CropConditionService;
import com.greenario.cropcondition.api.exceptions.NotAuthorizedOnCropConditionException;
import com.greenario.cropcondition.api.exceptions.CropConditionAccessItemAlreadyExistException;
import com.greenario.cropcondition.api.exceptions.CropConditionAlreadyExistException;
import com.greenario.cropcondition.api.exceptions.CropConditionInUseException;
import com.greenario.cropcondition.api.exceptions.CropConditionNotFoundException;
import com.greenario.cropcondition.api.models.CropCondition;
import com.greenario.cropcondition.api.models.CropConditionAccessItem;
import com.greenario.cropcondition.api.models.CropConditionSummary;
import com.greenario.cropcondition.rest.assembler.CropConditionAccessItemRepresentationModelAssembler;
import com.greenario.cropcondition.rest.assembler.CropConditionRepresentationModelAssembler;
import com.greenario.cropcondition.rest.assembler.CropConditionSummaryRepresentationModelAssembler;
import com.greenario.cropcondition.rest.models.DistributionTargetsCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionAccessItemCollectionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionRepresentationModel;
import com.greenario.cropcondition.rest.models.CropConditionSummaryPagedRepresentationModel;

/** Controller for all {@link CropCondition} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class CropConditionController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CropConditionController.class);

  private final CropConditionService cropConditionService;
  private final CropConditionRepresentationModelAssembler cropConditionRepresentationModelAssembler;
  private final CropConditionSummaryRepresentationModelAssembler cropConditionSummaryRepresentationModelAssembler;
  private final CropConditionAccessItemRepresentationModelAssembler cropConditionAccessItemRepresentationModelAssembler;

  @Autowired
  CropConditionController(
      CropConditionService cropConditionService,
      CropConditionRepresentationModelAssembler cropConditionRepresentationModelAssembler,
      CropConditionSummaryRepresentationModelAssembler cropConditionSummaryRepresentationModelAssembler,
      CropConditionAccessItemRepresentationModelAssembler cropConditionAccessItemRepresentationModelAssembler) {
    this.cropConditionService = cropConditionService;
    this.cropConditionRepresentationModelAssembler = cropConditionRepresentationModelAssembler;
    this.cropConditionSummaryRepresentationModelAssembler = cropConditionSummaryRepresentationModelAssembler;

    this.cropConditionAccessItemRepresentationModelAssembler = cropConditionAccessItemRepresentationModelAssembler;
  }

  /**
   * This endpoint retrieves a list of existing CropConditions. Filters can be
   * applied.
   *
   * @title Get a list of all CropConditions
   * @param request         the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter   the sort parameters
   * @param pagingParameter the paging parameters
   * @return the CropConditions with the given filter, sort and paging options.
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CropConditionSummaryPagedRepresentationModel> getCropConditions(
      HttpServletRequest request,
      CropConditionQueryFilterParameter filterParameter,
      CropConditionQuerySortParameter sortParameter,
      QueryPagingParameter<CropConditionSummary, CropConditionQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        CropConditionQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    CropConditionQuery query = cropConditionService.createCropConditionQuery();
    filterParameter.apply(query);
    sortParameter.apply(query);

    List<CropConditionSummary> cropConditionSummaries = pagingParameter.apply(query);
    CropConditionSummaryPagedRepresentationModel pagedModels = cropConditionSummaryRepresentationModelAssembler
        .toPagedModel(
            cropConditionSummaries, pagingParameter.getPageMetadata());

    return ResponseEntity.ok(pagedModels);
  }

  /**
   * This endpoint retrieves a single CropCondition.
   *
   * @title Get a single CropCondition
   * @param cropconditionid the Id of the requested CropCondition
   * @return the requested CropCondition
   * @throws CropConditionNotFoundException        if the requested CropCondition
   *                                               is
   *                                               not found
   * @throws NotAuthorizedOnCropConditionException if the current user has no
   *                                               permissions to access the
   *                                               requested CropCondition
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION_ID, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CropConditionRepresentationModel> getCropCondition(
      @PathVariable(value = "cropconditionid") String cropconditionid)
      throws CropConditionNotFoundException, NotAuthorizedOnCropConditionException {
    CropCondition cropCondition = cropConditionService.getCropCondition(cropconditionid);

    return ResponseEntity.ok(cropConditionRepresentationModelAssembler.toModel(cropCondition));
  }

  /**
   * This endpoint deletes an existing CropCondition.
   *
   * <p>
   * Returned HTTP Status codes:
   *
   * <ul>
   * <li><b>204 NO_CONTENT</b> - CropCondition has been deleted successfully
   * <li><b>202 ACCEPTED</b> - CropCondition still contains completed
   * CropConditionMonitorings. It
   * has
   * been marked for
   * deletion and will be deleted automatically as soon as all completed
   * CropConditionMonitorings
   * are
   * deleted.
   * <li><b>423 LOCKED</b> - CropCondition contains non-completed
   * CropConditionMonitorings and
   * cannot
   * be deleted.
   * </ul>
   *
   * @title Delete a CropCondition
   * @param cropconditionid the Id of the CropCondition which should be deleted
   * @return the deleted CropCondition
   * @throws NotAuthorizedOnCropConditionException if the current user is not
   *                                               authorized to delete this
   *                                               CropCondition.
   * @throws InvalidArgumentException              if the requested CropCondition
   *                                               Id
   *                                               is null or empty
   * @throws CropConditionNotFoundException        if the requested CropCondition
   *                                               is
   *                                               not found
   * @throws CropConditionInUseException           if the CropCondition contains
   *                                               cropConditionMonitorings.
   * @throws NotAuthorizedException                if the current user has not
   *                                               correct permissions
   */
  @DeleteMapping(path = RestEndpoints.URL_CROPCONDITION_ID)
  @Transactional(rollbackFor = Exception.class, noRollbackFor = CropConditionNotFoundException.class)
  public ResponseEntity<CropConditionRepresentationModel> deleteCropCondition(
      @PathVariable(value = "cropconditionid") String cropconditionid)
      throws InvalidArgumentException,
      CropConditionNotFoundException,
      CropConditionInUseException,
      NotAuthorizedException,
      NotAuthorizedOnCropConditionException {

    boolean cropConditionDeleted = cropConditionService.deleteCropCondition(cropconditionid);

    if (cropConditionDeleted) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("CropCondition successfully deleted.");
      }
      return ResponseEntity.noContent().build();
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "CropCondition was only marked for deletion and will be physically deleted later on.");
      }
      return ResponseEntity.accepted().build();
    }
  }

  /**
   * This endpoint creates a persistent CropCondition.
   *
   * @title Create a new CropCondition
   * @param cropConditionRepresentationModel the CropCondition which should be
   *                                         created.
   * @return the created CropCondition
   * @throws InvalidArgumentException           if some required properties of the
   *                                            CropCondition are not set.
   * @throws NotAuthorizedException             if the current user is not member
   *                                            of
   *                                            role BUSINESS_ADMIN or
   *                                            ADMIN
   * @throws CropConditionAlreadyExistException if the CropCondition exists
   *                                            already
   * @throws DomainNotFoundException            if the domain does not exist in
   *                                            the
   *                                            configuration.
   */
  @PostMapping(path = RestEndpoints.URL_CROPCONDITION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CropConditionRepresentationModel> createCropCondition(
      @RequestBody CropConditionRepresentationModel cropConditionRepresentationModel)
      throws InvalidArgumentException,
      NotAuthorizedException,
      CropConditionAlreadyExistException,
      DomainNotFoundException {
    CropCondition cropCondition = cropConditionRepresentationModelAssembler
        .toEntityModel(cropConditionRepresentationModel);
    cropCondition = cropConditionService.createCropCondition(cropCondition);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cropConditionRepresentationModelAssembler.toModel(cropCondition));
  }

  /**
   * This endpoint updates a given CropCondition.
   *
   * @title Update a CropCondition
   * @param cropconditionid                  the Id of the CropCondition which
   *                                         should
   *                                         be updated.
   * @param cropConditionRepresentationModel the new CropCondition for the
   *                                         requested
   *                                         id.
   * @return the updated CropCondition
   * @throws InvalidArgumentException              if the requested Id and the Id
   *                                               within the new CropCondition do
   *                                               not match.
   * @throws CropConditionNotFoundException        if the requested cropCondition
   *                                               does
   *                                               not
   * @throws NotAuthorizedException                if the current user is not
   *                                               authorized to update the
   *                                               CropCondition
   * @throws ConcurrencyException                  if an attempt is made to update
   *                                               the CropCondition and another
   *                                               user
   *                                               updated it already
   * @throws NotAuthorizedOnCropConditionException if the current user has not
   *                                               correct permissions
   */
  @PutMapping(path = RestEndpoints.URL_CROPCONDITION_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CropConditionRepresentationModel> updateCropCondition(
      @PathVariable(value = "cropconditionid") String cropconditionid,
      @RequestBody CropConditionRepresentationModel cropConditionRepresentationModel)
      throws CropConditionNotFoundException,
      NotAuthorizedException,
      ConcurrencyException,
      InvalidArgumentException,
      NotAuthorizedOnCropConditionException {
    if (!cropconditionid.equals(cropConditionRepresentationModel.getCropConditionId())) {
      throw new InvalidArgumentException(
          "Target-WB-ID('"
              + cropconditionid
              + "') is not identical with the WB-ID of to object which should be updated. ID=('"
              + cropConditionRepresentationModel.getCropConditionId()
              + "')");
    }
    CropCondition cropCondition = cropConditionRepresentationModelAssembler
        .toEntityModel(cropConditionRepresentationModel);
    cropCondition = cropConditionService.updateCropCondition(cropCondition);

    return ResponseEntity.ok(cropConditionRepresentationModelAssembler.toModel(cropCondition));
  }

  /**
   * This endpoint retrieves all CropCondition Access Items for a given
   * CropCondition.
   *
   * @title Get all CropCondition Access Items
   * @param cropconditionid the Id of the requested CropCondition.
   * @return the access items for the requested CropCondition.
   * @throws NotAuthorizedException                if the current user is not
   *                                               member
   *                                               of role BUSINESS_ADMIN or
   *                                               ADMIN
   * @throws CropConditionNotFoundException        if the requested CropCondition
   *                                               does
   *                                               not exist.
   * @throws NotAuthorizedOnCropConditionException if the current user has not
   *                                               correct permissions
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION_ID_ACCESS_ITEMS, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<CropConditionAccessItemCollectionRepresentationModel> getCropConditionAccessItems(
      @PathVariable(value = "cropconditionid") String cropconditionid)
      throws CropConditionNotFoundException,
      NotAuthorizedException,
      NotAuthorizedOnCropConditionException {
    List<CropConditionAccessItem> accessItems = cropConditionService.getCropConditionAccessItems(cropconditionid);

    return ResponseEntity.ok(
        cropConditionAccessItemRepresentationModelAssembler
            .toGreenarioCollectionModelForSingleCropCondition(cropconditionid, accessItems));
  }

  /**
   * This endpoint replaces all CropCondition Access Items for a given
   * CropCondition
   * with the provided
   * ones.
   *
   * @title Set all CropCondition Access Items
   * @param cropconditionid                  the Id of the CropCondition whose
   *                                         CropCondition Access Items will be
   *                                         replaced
   * @param cropConditionAccessItemRepModels the new CropCondition Access Items.
   * @return the new CropCondition Access Items for the requested CropCondition
   * @throws NotAuthorizedException                       if the current user is
   *                                                      not
   *                                                      member of role
   *                                                      BUSINESS_ADMIN or
   *                                                      ADMIN
   * @throws InvalidArgumentException                     if the new CropCondition
   *                                                      Access Items are not
   *                                                      provided.
   * @throws CropConditionNotFoundException               TODO: this is never
   *                                                      thrown.
   * @throws CropConditionAccessItemAlreadyExistException if a duplicate
   *                                                      CropCondition
   *                                                      Access Item exists
   *                                                      in the provided list.
   * @throws NotAuthorizedOnCropConditionException        if the current user has
   *                                                      not correct permissions
   */
  @PutMapping(path = RestEndpoints.URL_CROPCONDITION_ID_ACCESS_ITEMS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<CropConditionAccessItemCollectionRepresentationModel> setCropConditionAccessItems(
      @PathVariable(value = "cropconditionid") String cropconditionid,
      @RequestBody CropConditionAccessItemCollectionRepresentationModel cropConditionAccessItemRepModels)
      throws InvalidArgumentException,
      CropConditionNotFoundException,
      CropConditionAccessItemAlreadyExistException,
      NotAuthorizedException,
      NotAuthorizedOnCropConditionException {
    if (cropConditionAccessItemRepModels == null) {
      throw new InvalidArgumentException("Can't create something with NULL body-value.");
    }

    List<CropConditionAccessItem> wbAccessItems = cropConditionAccessItemRepModels.getContent().stream()
        .map(cropConditionAccessItemRepresentationModelAssembler::toEntityModel)
        .collect(Collectors.toList());
    cropConditionService.setCropConditionAccessItems(cropconditionid, wbAccessItems);
    List<CropConditionAccessItem> updatedWbAccessItems = cropConditionService
        .getCropConditionAccessItems(cropconditionid);

    return ResponseEntity.ok(
        cropConditionAccessItemRepresentationModelAssembler
            .toGreenarioCollectionModelForSingleCropCondition(cropconditionid, updatedWbAccessItems));
  }

  /**
   * This endpoint retrieves all Distribution Targets for a requested
   * CropCondition.
   *
   * @title Get all Distribution Targets for a CropCondition
   * @param cropconditionid the Id of the CropCondition whose Distribution Targets
   *                        will
   *                        be retrieved
   * @return the Distribution Targets for the requested CropCondition
   * @throws CropConditionNotFoundException        if the requested CropCondition
   *                                               does
   *                                               not exist.
   * @throws NotAuthorizedOnCropConditionException if the current user has no read
   *                                               permission for the
   *                                               specified CropCondition
   */
  @GetMapping(path = RestEndpoints.URL_CROPCONDITION_ID_DISTRIBUTION, produces = MediaTypes.HAL_JSON_VALUE)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel> getDistributionTargets(
      @PathVariable(value = "cropconditionid") String cropconditionid)
      throws CropConditionNotFoundException, NotAuthorizedOnCropConditionException {
    List<CropConditionSummary> distributionTargets = cropConditionService.getDistributionTargets(cropconditionid);
    DistributionTargetsCollectionRepresentationModel distributionTargetRepModels = cropConditionSummaryRepresentationModelAssembler
        .toGreenarioCollectionModel(distributionTargets);

    return ResponseEntity.ok(distributionTargetRepModels);
  }

  /**
   * This endpoint replaces all Distribution Targets for a given CropCondition
   * with
   * the provided ones.
   *
   * @title Set all Distribution Targets for a CropCondition
   * @param sourceCropConditionId  the source CropCondition
   * @param targetCropConditionIds the destination CropConditions.
   * @return the new Distribution Targets for the requested CropCondition.
   * @throws CropConditionNotFoundException        if any CropCondition was not
   *                                               found
   *                                               (either source or target)
   * @throws NotAuthorizedOnCropConditionException if the current user doesn't
   *                                               have
   *                                               READ permission for
   *                                               the source CropCondition
   * @throws NotAuthorizedException                if the current user has not
   *                                               correct permissions
   */
  @PutMapping(path = RestEndpoints.URL_CROPCONDITION_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<DistributionTargetsCollectionRepresentationModel> setDistributionTargetsForCropConditionId(
      @PathVariable(value = "cropconditionid") String sourceCropConditionId,
      @RequestBody List<String> targetCropConditionIds)
      throws CropConditionNotFoundException,
      NotAuthorizedException,
      NotAuthorizedOnCropConditionException {
    cropConditionService.setDistributionTargets(sourceCropConditionId, targetCropConditionIds);

    List<CropConditionSummary> distributionTargets = cropConditionService.getDistributionTargets(sourceCropConditionId);

    return ResponseEntity.ok(
        cropConditionSummaryRepresentationModelAssembler.toGreenarioCollectionModel(
            distributionTargets));
  }

  /**
   * This endpoint removes all Distribution Target references for a provided
   * CropCondition.
   *
   * @title Remove a CropCondition as Distribution Target
   * @param targetCropConditionId the Id of the requested CropCondition.
   * @return no content
   * @throws CropConditionNotFoundException        if the requested CropCondition
   *                                               does
   *                                               not exist.
   * @throws NotAuthorizedException                if the requested user ist not
   *                                               ADMIN or BUSINESS_ADMIN.
   * @throws NotAuthorizedOnCropConditionException if the current user has not
   *                                               correct permissions
   */
  @DeleteMapping(path = RestEndpoints.URL_CROPCONDITION_ID_DISTRIBUTION)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<Void> removeDistributionTargetForCropConditionId(
      @PathVariable(value = "cropconditionid") String targetCropConditionId)
      throws CropConditionNotFoundException,
      NotAuthorizedOnCropConditionException,
      NotAuthorizedException {
    List<CropConditionSummary> sourceCropConditions = cropConditionService
        .getDistributionSources(targetCropConditionId);
    for (CropConditionSummary source : sourceCropConditions) {
      cropConditionService.removeDistributionTarget(source.getId(), targetCropConditionId);
    }

    return ResponseEntity.noContent().build();
  }

  public enum CropConditionQuerySortBy implements QuerySortBy<CropConditionQuery> {
    NAME(CropConditionQuery::orderByName),
    KEY(CropConditionQuery::orderByKey),
    OWNER(CropConditionQuery::orderByOwner),
    TYPE(CropConditionQuery::orderByType),
    DESCRIPTION(CropConditionQuery::orderByDescription),
    CUSTOM_1((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_1, sort)),
    CUSTOM_2((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_2, sort)),
    CUSTOM_3((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_3, sort)),
    CUSTOM_4((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_4, sort)),
    CUSTOM_5((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_5, sort)),
    CUSTOM_6((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_6, sort)),
    CUSTOM_7((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_7, sort)),
    CUSTOM_8((query, sort) -> query.orderByCustomAttribute(CropConditionCustomField.CUSTOM_8, sort)),
    DOMAIN(CropConditionQuery::orderByDomain),
    ORG_LEVEL_1(CropConditionQuery::orderByOrgLevel1),
    ORG_LEVEL_2(CropConditionQuery::orderByOrgLevel2),
    ORG_LEVEL_3(CropConditionQuery::orderByOrgLevel3),
    ORG_LEVEL_4(CropConditionQuery::orderByOrgLevel4);

    private final BiConsumer<CropConditionQuery, SortDirection> consumer;

    CropConditionQuerySortBy(BiConsumer<CropConditionQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(CropConditionQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the
  // generic 'sort-by'
  // parameter from the super class.
  public static class CropConditionQuerySortParameter
      extends QuerySortParameter<CropConditionQuery, CropConditionQuerySortBy> {

    @ConstructorProperties({ "sort-by", "order" })
    public CropConditionQuerySortParameter(
        List<CropConditionQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<CropConditionQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
