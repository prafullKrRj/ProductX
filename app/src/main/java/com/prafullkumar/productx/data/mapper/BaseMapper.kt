package com.prafullkumar.productx.data.mapper


interface BaseMapper<DTO, DomainModel> {
    fun mapToDomainModel(dto: DTO): DomainModel
    fun mapFromDomainModel(domainModel: DomainModel): DTO
}