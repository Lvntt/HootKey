package dev.banger.hootkey.domain.entity.template

data class CreateTemplateRequest(
    val name: String,
    val fields: List<TemplateField>,
)