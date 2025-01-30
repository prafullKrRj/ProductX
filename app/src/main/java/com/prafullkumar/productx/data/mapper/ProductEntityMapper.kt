package com.prafullkumar.productx.data.mapper

import com.prafullkumar.productx.data.local.ProductEntity
import com.prafullkumar.productx.domain.model.Product

/**
 * Mapper class to convert between ProductEntity and Product domain model.
 */
class ProductEntityMapper : BaseMapper<ProductEntity, Product> {

    /**
     * Maps a ProductEntity to a Product domain model.
     *
     * @param dto The ProductEntity to be mapped.
     * @return The mapped Product domain model.
     */
    override fun mapToDomainModel(dto: ProductEntity): Product {
        return Product(
            id = dto.id,
            image = dto.image,
            price = dto.price,
            productName = dto.productName,
            productType = dto.productType,
            tax = dto.tax
        )
    }

    /**
     * Maps a Product domain model to a ProductEntity.
     *
     * @param domainModel The Product domain model to be mapped.
     * @return The mapped ProductEntity.
     */
    override fun mapFromDomainModel(domainModel: Product): ProductEntity {
        return ProductEntity(
            image = domainModel.image,
            price = domainModel.price ?: 0.0,
            productName = domainModel.productName,
            productType = domainModel.productType,
            tax = domainModel.tax ?: 0.0
        )
    }
}