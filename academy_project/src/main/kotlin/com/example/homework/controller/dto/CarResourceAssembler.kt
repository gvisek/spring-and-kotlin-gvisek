package com.example.homework.controller.dto

import com.example.homework.controller.CarController
import com.example.homework.controller.CheckUpController
import com.example.homework.entity.ManufacturerModel
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport
import java.time.LocalDate
import java.util.*
import com.example.homework.entity.CarDetailsResponse
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.stereotype.Component


@Component
class CarResourceAssembler: RepresentationModelAssemblerSupport<CarDetailsResponse, CarResource>(
    CarController::class.java, CarResource::class.java
) {

    private val pagination = Pageable.ofSize(20)

    override fun toModel(entity: CarDetailsResponse): CarResource {
        return createModelWithId(entity.car.id, entity).apply {
            add(
                linkTo<CheckUpController> {
                    getAllCheckUpsForCarPaged(entity.car.id, "asc", pagination )
                }.withRel("checkUps")
            )
        }
    }

    override fun instantiateModel(entity: CarDetailsResponse): CarResource {
        return CarResource(
            entity.car.id, entity.car.date, entity.car.carDetails, entity.car.productionYear, entity.car.vin, entity.checkupNecessary
        )
    }
}

@Relation(collectionRelation = IanaLinkRelations.ITEM_VALUE)
data class CarResource(
    val id: UUID,

    val date: LocalDate,

    val carDetails: ManufacturerModel,

    val productionYear: Int,

    val vin: String,

    val checkupNecessary: Boolean
): RepresentationModel<CarResource>()