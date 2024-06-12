package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.Template

interface TemplateRepository {

    suspend fun getAll(): List<Template>

    suspend fun create(template: CreateTemplateRequest): Template

    suspend fun delete(id: String)

}