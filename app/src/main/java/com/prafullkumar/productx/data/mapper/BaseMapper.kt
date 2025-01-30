package com.prafullkumar.productx.data.mapper

/**
 * Interface for mapping between Data Transfer Objects (DTO) and Domain Models.
 *
 * @param DTO The type of the Data Transfer Object.
 * @param DomainModel The type of the Domain Model.
 */
interface BaseMapper<DTO, DomainModel> {
    /**
     * Maps a Data Transfer Object to a Domain Model.
     *
     * @param dto The Data Transfer Object to be mapped.
     * @return The mapped Domain Model.
     */
    fun mapToDomainModel(dto: DTO): DomainModel

    /**
     * Maps a Domain Model to a Data Transfer Object.
     *
     * @param domainModel The Domain Model to be mapped.
     * @return The mapped Data Transfer Object.
     */
    fun mapFromDomainModel(domainModel: DomainModel): DTO
}