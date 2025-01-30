package com.prafullkumar.productx.data.mapper

import com.prafullkumar.productx.data.remote.dto.productGettingDtos.ProductResponseItemDto
import com.prafullkumar.productx.domain.model.Product

val images = listOf<String>(
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738107592_0_temp_image_1738107591899.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106971_0_temp_image_1738106970691.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106363_0_temp_image_1738106363171.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106114_0_temp_image_1738106114070.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106068_0_temp_image_1738106068076.jpg"
)

class ProductResponseMapper : BaseMapper<ProductResponseItemDto, Product> {
    override fun mapToDomainModel(dto: ProductResponseItemDto): Product {
        val image =
            if (dto.image == null) images.random() else if (dto.image.isEmpty()) images.random() else dto.image
        return Product(
            image = image,
            price = dto.price ?: 0.0,
            productName = dto.product_name ?: "Product Name",
            productType = dto.product_type ?: "Product Type",
            tax = dto.tax ?: 18.0
        )
    }

    override fun mapFromDomainModel(domainModel: Product): ProductResponseItemDto {
        return ProductResponseItemDto(
            image = domainModel.image,
            price = domainModel.price,
            product_name = domainModel.productName,
            product_type = domainModel.productType,
            tax = domainModel.tax
        )
    }
}