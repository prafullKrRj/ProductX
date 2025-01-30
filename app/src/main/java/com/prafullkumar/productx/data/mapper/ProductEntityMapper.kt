package com.prafullkumar.productx.data.mapper

import com.prafullkumar.productx.data.local.ProductEntity
import com.prafullkumar.productx.domain.model.Product

class ProductEntityMapper : BaseMapper<ProductEntity, Product> {
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