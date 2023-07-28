package com.example.homework

import com.example.homework.entity.ManufacturerModel
import com.example.homework.repository.ManufacturerModelRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ManufacturerModelRepositoryTest {

    @Autowired
    private lateinit var manufacturerModelRepository: ManufacturerModelRepository

    @Test
    fun `Test saving and retrieving ManufacturerModel`() {
        val manufacturer = "TestManufacturer"
        val model = "TestModel"
        val savedModel = manufacturerModelRepository.save(ManufacturerModel(manufacturer = manufacturer, model = model))

        assertNotNull(savedModel.id)

        val retrievedModel = manufacturerModelRepository.findManufacturerModelByManufacturerAndModel(manufacturer, model)
        assertNotNull(retrievedModel)
        assertEquals(manufacturer, retrievedModel.manufacturer)
        assertEquals(model, retrievedModel.model)
        assertEquals(savedModel.id, retrievedModel.id)
    }

    @Test
    fun `Test existsManufacturerModelByManufacturerAndModel`() {
        val manufacturer = "TestManufacturer"
        val model = "TestModel"
        assertFalse(manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(manufacturer, model))

        manufacturerModelRepository.save(ManufacturerModel(manufacturer = manufacturer, model = model))
        assertTrue(manufacturerModelRepository.existsManufacturerModelByManufacturerAndModel(manufacturer, model))
    }

    @Test
    fun `Test findAll`() {
        val manufacturer1 = "Manufacturer1"
        val model1 = "Model1"
        val manufacturer2 = "Manufacturer2"
        val model2 = "Model2"

        manufacturerModelRepository.save(ManufacturerModel(manufacturer = manufacturer1, model = model1))
        manufacturerModelRepository.save(ManufacturerModel(manufacturer = manufacturer2, model = model2))

        val allModels = manufacturerModelRepository.findAll()
        assertEquals(2, allModels.size)
    }
}