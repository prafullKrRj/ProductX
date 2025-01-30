package com.prafullkumar.productx.data.mapper

import com.prafullkumar.productx.data.remote.dto.productGettingDtos.ProductResponseItemDto
import com.prafullkumar.productx.domain.model.Product

// List of placeholder images to use when the product image is null or empty
val images = listOf<String>(
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738107592_0_temp_image_1738107591899.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106971_0_temp_image_1738106970691.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106363_0_temp_image_1738106363171.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106114_0_temp_image_1738106114070.jpg",
    "https://vx-erp-product-images.s3.ap-south-1.amazonaws.com/9_1738106068_0_temp_image_1738106068076.jpg"
)

/**
 * Mapper class to convert between ProductResponseItemDto and Product domain model.
 */
class ProductResponseMapper : BaseMapper<ProductResponseItemDto, Product> {

    /**
     * Maps a ProductResponseItemDto to a Product domain model.
     * If the image in the DTO is null or empty, a random placeholder image is used.
     *
     * @param dto The ProductResponseItemDto to be mapped.
     * @return The mapped Product domain model.
     */
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

    /**
     * Maps a Product domain model to a ProductResponseItemDto.
     *
     * @param domainModel The Product domain model to be mapped.
     * @return The mapped ProductResponseItemDto.
     */
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