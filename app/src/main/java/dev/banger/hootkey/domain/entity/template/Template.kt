package dev.banger.hootkey.domain.entity.template

data class Template(
    val id: String,
    val name: String,
    val fields: List<TemplateField>,
)
