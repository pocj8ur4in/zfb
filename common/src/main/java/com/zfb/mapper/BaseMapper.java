package com.zfb.mapper;

import java.util.List;

/**
 * MapStruct base mapper interface
 *
 * @param <E> Entity
 * @param <D> DTO
 */
public interface BaseMapper<E, D> {

  /**
   * Convert Entity to DTO
   *
   * @param entity Entity
   * @return DTO
   */
  D toDto(E entity);

  /**
   * Convert DTO to Entity
   *
   * @param dto DTO
   * @return Entity
   */
  E toEntity(D dto);

  /**
   * Convert Entity List to DTO List
   *
   * @param entities Entity List
   * @return DTO List
   */
  List<D> toDtoList(List<E> entities);

  /**
   * Convert DTO List to Entity List
   *
   * @param dtos DTO List
   * @return Entity List
   */
  List<E> toEntityList(List<D> dtos);
}
