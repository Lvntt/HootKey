package dev.banger.hootkey.domain.repository

import dev.banger.hootkey.domain.entity.template.CreateTemplateRequest
import dev.banger.hootkey.domain.entity.template.Template

interface TemplateRepository {

    fun getAll(): List<Template>

    fun create(template: CreateTemplateRequest): Template

    fun delete(id: String)

}