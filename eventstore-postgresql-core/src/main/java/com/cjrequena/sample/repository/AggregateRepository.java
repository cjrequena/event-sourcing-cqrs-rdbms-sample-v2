package com.cjrequena.sample.repository;

import com.cjrequena.sample.entity.AggregateEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * <p></p>
 * <p></p>
 * @author cjrequena
 */
@Repository
public interface AggregateRepository extends CrudRepository<AggregateEntity, UUID> {

  // Native SQL query to insert if not exists (using PostgreSQL's ON CONFLICT DO NOTHING)
  @Modifying
  @Transactional
  @Query(value = """
    INSERT INTO es_aggregate (id, aggregate_type, aggregate_version)
    VALUES (:aggregateId, :aggregateType, 0)
    ON CONFLICT (id) DO NOTHING
    """, nativeQuery = true)
  void createAggregateIfAbsent(
    @Param("aggregateId") UUID aggregateId,
    @Param("aggregateType") String aggregateType
  );

  @Transactional
  @Query(value = """
    UPDATE es_aggregate
       SET aggregate_version = :newAggregateVersion
     WHERE id = :aggregateId
       AND aggregate_version = :expectedAggregateVersion
    RETURNING 1
    """, nativeQuery = true)
  Optional<Integer> validateAndUpdateAggregateVersionIfMatch(
    @Param("aggregateId") UUID aggregateId,
    @Param("expectedAggregateVersion") long expectedAggregateVersion,
    @Param("newAggregateVersion") long newAggregateVersion
  );

  @Query(value = "SELECT CASE "
    + " WHEN  VERSION = :#{#aggregate.version} THEN True ELSE False END "
    + " FROM AGGREGATE WHERE ID=:#{#aggregate.id}",
    nativeQuery = true)
  boolean validateAggregateVersion(@Param("aggregate") AggregateEntity aggregateEntity);

  @Query(value = "SELECT CASE "
    + " WHEN COUNT(A)> 0 THEN TRUE ELSE FALSE END "
    + " FROM AGGREGATE A "
    + " WHERE A.ID = :id AND A.AGGREGATE_TYPE = :aggregateType",
    nativeQuery = true)
  boolean verifyAggregate(@Param("id") UUID id, @Param("aggregateType") String aggregateType);

}
