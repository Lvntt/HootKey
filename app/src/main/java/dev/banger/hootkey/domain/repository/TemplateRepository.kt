package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.Template
import dev.banger.hootkey.domain.entity.template.TemplateShort

interface TemplateRepository {

    suspend fun getById(id: String): Template?

    suspend fun templateExists(id: String): Boolean

    /**
     * Returns all templates including all fields, may be slow
     * Use getAllShort and/or getById for specific template where possible instead
     */
    suspend fun getAllFull(): List<Template>

    suspend fun getAllShort(): List<TemplateShort>

    suspend fun create(template: CreateTemplateRequest): Template

    suspend fun delete(id: String)

}